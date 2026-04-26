package com.wordle.schedule;

import com.wordle.mapper.DailyWordMapper;
import com.wordle.model.DailyWord;
import com.wordle.model.Word;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Component
public class WordScheduler {

    private static final Logger log            = Logger.getLogger(WordScheduler.class.getName());
    private static final String CLAUDE_API_URL = "https://api.anthropic.com/v1/messages";

    private static final Word   FALLBACK_WORD  = new Word("CRANE");

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();
    private final HttpClient      httpClient;
    private final DailyWordMapper dailyWordMapper;

    @Value("${anthropic.key}")
    private String apiKey;

    public WordScheduler(DailyWordMapper dailyWordMapper) {
        this.dailyWordMapper = dailyWordMapper;
        this.httpClient      = HttpClient.newHttpClient();
    }

    @PostConstruct
    public void start() {
        // 서버 시작 시 오늘/내일 단어 없으면 즉시 생성
        ensureWordForDate(LocalDate.now());
        ensureWordForDate(LocalDate.now().plusDays(1));

        // 매일 23:50 에 다음날 단어 미리 생성
        scheduleDaily();
        log.info("WordScheduler 시작 - 매일 23:50 실행");
    }

    @PreDestroy
    public void stop() {
        scheduler.shutdown();
        log.info("WordScheduler 종료");
    }

    // 특정 날짜 단어 없으면 즉시 생성
    public void ensureWordForDate(LocalDate date) {
        if (dailyWordMapper.findByPlayDate(date) == null) {
            log.info(date + " 단어 없음 → 즉시 생성");
            fetchAndSave(date);
        }
    }

    // 오늘 단어 조회 (없으면 즉시 생성 후 반환)
    public Word getTodayWord() {
        var today = LocalDate.now();
        ensureWordForDate(today);  // 없으면 즉시 생성

        var daily = dailyWordMapper.findByPlayDate(today);

        if (daily == null) {
            System.out.println("API_KEY : "+ apiKey);
            // API 실패 시 fallback
            log.warning("오늘 단어 조회 실패 → fallback 사용");
            return FALLBACK_WORD;
        }

        return new Word(daily.getWord());
    }

    private void scheduleDaily() {
        var now          = LocalTime.now();
        var target       = LocalTime.of(23, 50);
        var initialDelay = now.isBefore(target)
                ? now.until(target, ChronoUnit.SECONDS)
                : now.until(target.plusHours(24), ChronoUnit.SECONDS);

        scheduler.scheduleAtFixedRate(
                () -> ensureWordForDate(LocalDate.now().plusDays(1)),
                initialDelay,
                TimeUnit.DAYS.toSeconds(1),
                TimeUnit.SECONDS
        );
    }

    private void fetchAndSave(LocalDate targetDate) {
        try {
            // 기존 단어 목록 조회
            var usedWords = dailyWordMapper.findAllWords();
            log.info("기존 사용된 단어 수: " + usedWords.size());

            // 중복되지 않는 단어 받아오기 (최대 5번 시도)
            Word word = null;
            int maxAttempts = 5;

            for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                var candidate = callClaudeApi(usedWords);
                if (!usedWords.contains(candidate.value())) {
                    word = candidate;
                    log.info("새 단어 확정 (시도 " + attempt + "회): " + word.value());
                    break;
                }
                log.warning("중복 단어 감지 (시도 " + attempt + "회): " + candidate.value());
            }

            // 5번 시도 후에도 중복이면 fallback
            if (word == null) {
                log.warning("중복 제거 실패 → fallback 사용");
                word = FALLBACK_WORD;
            }

            var dailyWord = DailyWord.builder()
                    .word(word.value())
                    .playDate(targetDate)
                    .build();
            dailyWordMapper.insertDailyWord(dailyWord);
            log.info("단어 저장 완료: " + targetDate + " → " + word.value());

        } catch (Exception e) {
            log.warning("Claude API 실패: " + e.getMessage());
        }
    }

    // Claude API 호출 시 기존 단어 목록 전달
    private Word callClaudeApi(List<String> usedWords) throws Exception {
        // 기존 단어 목록을 프롬프트에 포함
        var usedWordsStr = usedWords.isEmpty()
                ? "없음"
                : String.join(", ", usedWords);

        var requestBody = """
        {
            "model": "claude-opus-4-5",
            "max_tokens": 10,
            "messages": [{
                "role": "user",
                "content": "Give me a single random 5-letter English word in uppercase. Reply with ONLY the word, nothing else. Do NOT use any of these words: %s"
            }]
        }
        """.formatted(usedWordsStr);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(CLAUDE_API_URL))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .header("x-api-key", apiKey)
                .header("anthropic-version", "2023-06-01")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return parseWord(response.body());
    }

    private Word parseWord(String body) {
        var start = body.indexOf("\"text\":\"") + 8;
        var end   = body.indexOf("\"", start);
        return new Word(body.substring(start, end).trim());
    }
}
