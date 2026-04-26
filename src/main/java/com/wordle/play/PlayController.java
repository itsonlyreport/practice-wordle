package com.wordle.play;

import com.wordle.gameResult.service.GameResultService;
import com.wordle.model.User;
import com.wordle.model.Word;
import com.wordle.model.WordleGame;
import com.wordle.schedule.WordScheduler;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;

@Controller
@RequestMapping("/play")
public class PlayController {

    @Autowired
    private WordScheduler wordScheduler;

    @Autowired
    private GameResultService gameResultService;

    @GetMapping
    public String index(@RequestParam(required = false) String action,
                        HttpSession session,
                        HttpServletRequest req,
                        Model model) {

        if (session.getAttribute("loginUser") == null) return "redirect:/";

        var loginUser = (User) session.getAttribute("loginUser");
        var today     = LocalDate.now().toString();

        if (hasDoneCookie(req, today)) {
            model.addAttribute("alreadyDone", true);
            model.addAttribute("loginUser", loginUser);
            return "play";
        }

        var game = getGame(session);

        System.out.println("=== PlayController ===");
        System.out.println("game 존재 여부: " + game.isPresent());
        System.out.println("action: " + action);
        System.out.println("isNewDay: " + isNewDay(session));

        if ("new".equals(action) || game.isEmpty() || isNewDay(session)) {
            try {
                var word = wordScheduler.getTodayWord();
                System.out.println("오늘 단어: " + word.value());
                session.setAttribute("game", new WordleGame(word));
                session.setAttribute("gameDate", today);
            } catch (Exception e) {
                System.out.println("단어 가져오기 실패: " + e.getMessage());
                session.setAttribute("game", new WordleGame(new Word("CRANE")));
                session.setAttribute("gameDate", today);
            }
        }

        var finalGame = getGame(session).orElse(null);
        System.out.println("model에 넣는 game: " + finalGame);
        System.out.println("game.maxTries: " + (finalGame != null ? finalGame.getMaxTries() : "null"));

        model.addAttribute("game", finalGame);
        model.addAttribute("loginUser", loginUser);
        return "play";
    }

    @PostMapping
    public String submit(@RequestParam(required = false) String guess,
                         HttpSession session,
                         HttpServletRequest req,
                         HttpServletResponse resp,
                         Model model) {

        if (session.getAttribute("loginUser") == null) return "redirect:/";

        var loginUser = (User) session.getAttribute("loginUser");
        var today     = LocalDate.now().toString();

        // 이미 완료된 경우 차단
        if (hasDoneCookie(req, today)) {
            model.addAttribute("alreadyDone", true);
            model.addAttribute("loginUser", loginUser);
            return "play";
        }

        var game = getGame(session).orElseGet(() -> {
            var newGame = new WordleGame(wordScheduler.getTodayWord());
            session.setAttribute("game", newGame);
            return newGame;
        });

        var errorMsg = (guess != null && !guess.isBlank())
                ? game.submitGuess(guess)
                : "단어를 입력해주세요.";

        if (errorMsg != null) model.addAttribute("errorMsg", errorMsg);

        // 게임 종료 시
        if (game.isGameOver()) {
            // 공통: 쿠키 저장 (자정까지)
            setDoneCookie(resp, today);

            // 로그인 유저만 DB 저장
            if (!isGuest(loginUser)) {
                gameResultService.save(
                        loginUser,
                        game.getGuesses().size(),
                        game.isWin()
                );
            }
        }

        model.addAttribute("game", game);
        model.addAttribute("loginUser", loginUser);
        return "play";
    }

    // 오늘 완료 쿠키 확인
    private boolean hasDoneCookie(HttpServletRequest req, String today) {
        if (req.getCookies() == null) return false;
        return Arrays.stream(req.getCookies())
                .anyMatch(c -> "wordle_done".equals(c.getName())
                        && today.equals(c.getValue()));
    }

    // 완료 쿠키 설정 (자정까지 유지)
    private void setDoneCookie(HttpServletResponse resp, String today) {
        var cookie = new Cookie("wordle_done", today);
        cookie.setMaxAge(getSecondsUntilMidnight());
        cookie.setPath("/");
        resp.addCookie(cookie);
    }

    // 자정까지 남은 초 계산
    private int getSecondsUntilMidnight() {
        var now     = LocalTime.now();
        var seconds = (int) now.until(LocalTime.MIDNIGHT, ChronoUnit.SECONDS);
        return seconds <= 0 ? 86400 + seconds : seconds;
    }

    private boolean isGuest(User user) {
        return "GUEST".equals(user.getLoginType());
    }

    private Optional<WordleGame> getGame(HttpSession session) {
        return Optional.ofNullable(session.getAttribute("game"))
                .filter(WordleGame.class::isInstance)
                .map(WordleGame.class::cast);
    }

    private boolean isNewDay(HttpSession session) {
        var gameDate = (String) session.getAttribute("gameDate");
        return gameDate == null || !gameDate.equals(LocalDate.now().toString());
    }
}