package com.wordle.admin;

import com.wordle.mapper.DailyWordMapper;
import com.wordle.model.DailyWord;
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
@RequestMapping("/admin/words")
public class AdminWordController {

    @Autowired
    private DailyWordMapper dailyWordMapper;

    @Autowired
    private WordScheduler wordScheduler;

    // 단어 목록
    @GetMapping
    public String list(Model model) {
        model.addAttribute("words", dailyWordMapper.findAllOrderByDate());
        return "admin/words";
    }

    // 단어 수동 등록
    @PostMapping("/add")
    public String add(@RequestParam String word,
                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate playDate,
                      Model model) {

        // 날짜 중복 체크
        if (dailyWordMapper.findByPlayDate(playDate) != null) {
            model.addAttribute("errorMsg", "해당 날짜에 이미 단어가 있습니다.");
            model.addAttribute("words", dailyWordMapper.findAllOrderByDate());
            return "admin/words";
        }

        // 단어 중복 체크
        if (dailyWordMapper.findAllWords().contains(word.toUpperCase())) {
            model.addAttribute("errorMsg", "이미 사용된 단어입니다.");
            model.addAttribute("words", dailyWordMapper.findAllOrderByDate());
            return "admin/words";
        }

        var dailyWord = DailyWord.builder()
                .word(word.toUpperCase())
                .playDate(playDate)
                .build();
        dailyWordMapper.insertDailyWord(dailyWord);

        return "redirect:/admin/words";
    }

    // 단어 수정
    @PostMapping("/edit")
    public String edit(@RequestParam Long id,
                       @RequestParam String word,
                       Model model) {

        // 단어 중복 체크 (자기 자신 제외)
        var existing = dailyWordMapper.findById(id);
        var usedWords = dailyWordMapper.findAllWords();
        usedWords.remove(existing.getWord());

        if (usedWords.contains(word.toUpperCase())) {
            model.addAttribute("errorMsg", "이미 사용된 단어입니다.");
            model.addAttribute("words", dailyWordMapper.findAllOrderByDate());
            return "admin/words";
        }

        var dailyWord = DailyWord.builder()
                .id(id)
                .word(word.toUpperCase())
                .build();
        dailyWordMapper.updateDailyWord(dailyWord);

        return "redirect:/admin/words";
    }

    // 단어 삭제
    @PostMapping("/delete")
    public String delete(@RequestParam Long id) {
        dailyWordMapper.deleteDailyWord(id);
        return "redirect:/admin/words";
    }

    // Claude로 특정 날짜 단어 즉시 생성
    @PostMapping("/generate")
    public String generate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate playDate) {
        wordScheduler.ensureWordForDate(playDate);
        return "redirect:/admin/words";
    }
}