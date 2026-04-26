package com.wordle.admin;

import com.wordle.mapper.DailyWordMapper;
import com.wordle.schedule.WordScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/system")
public class AdminSystemController {

    @Autowired
    private WordScheduler   wordScheduler;

    @Autowired
    private DailyWordMapper dailyWordMapper;

    @GetMapping
    public String system(Model model) {
        // 오늘/내일 단어 존재 여부
        var today    = LocalDate.now();
        var tomorrow = today.plusDays(1);

        model.addAttribute("today", today);
        model.addAttribute("tomorrow", tomorrow);
        model.addAttribute("todayWord", dailyWordMapper.findByPlayDate(today));
        model.addAttribute("tomorrowWord", dailyWordMapper.findByPlayDate(tomorrow));
        return "system";
    }

    // 특정 날짜 단어 즉시 생성
    @PostMapping("/generate")
    public String generate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate playDate,
                           Model model) {
        try {
            // 이미 있으면 스킵
            if (dailyWordMapper.findByPlayDate(playDate) != null) {
                model.addAttribute("errorMsg", playDate + " 날짜에 이미 단어가 있습니다.");
            } else {
                wordScheduler.ensureWordForDate(playDate);
                model.addAttribute("successMsg", playDate + " 단어 생성 완료!");
            }
        } catch (Exception e) {
            model.addAttribute("errorMsg", "단어 생성 실패: " + e.getMessage());
        }

        return refreshSystem(model);
    }

    // 특정 날짜 단어 강제 재생성 (기존 삭제 후 새로 생성)
    @PostMapping("/regenerate")
    public String regenerate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate playDate,
                             Model model) {
        try {
            // 기존 단어 삭제 후 재생성
            var existing = dailyWordMapper.findByPlayDate(playDate);
            if (existing != null) {
                dailyWordMapper.deleteDailyWord(existing.getId());
            }
            wordScheduler.ensureWordForDate(playDate);
            model.addAttribute("successMsg", playDate + " 단어 재생성 완료!");
        } catch (Exception e) {
            model.addAttribute("errorMsg", "단어 재생성 실패: " + e.getMessage());
        }

        return refreshSystem(model);
    }

    // 향후 7일치 단어 일괄 생성
    @PostMapping("/generate-week")
    public String generateWeek(Model model) {
        var today   = LocalDate.now();
        int created = 0;
        int skipped = 0;

        for (int i = 0; i < 7; i++) {
            var date = today.plusDays(i);
            if (dailyWordMapper.findByPlayDate(date) == null) {
                wordScheduler.ensureWordForDate(date);
                created++;
            } else {
                skipped++;
            }
        }

        model.addAttribute("successMsg",
                "7일치 생성 완료! 생성: " + created + "개, 스킵: " + skipped + "개");

        return refreshSystem(model);
    }

    private String refreshSystem(Model model) {
        var today   = LocalDate.now();
        var tomorrow = today.plusDays(1);
        model.addAttribute("today", today);
        model.addAttribute("tomorrow", tomorrow);
        model.addAttribute("todayWord", dailyWordMapper.findByPlayDate(today));
        model.addAttribute("tomorrowWord", dailyWordMapper.findByPlayDate(tomorrow));
        return "system";
    }
}