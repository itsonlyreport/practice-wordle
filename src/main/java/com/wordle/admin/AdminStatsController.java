package com.wordle.admin;

import com.wordle.mapper.GameResultMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/stats")
public class AdminStatsController {

    @Autowired
    private GameResultMapper gameResultMapper;

    @GetMapping
    public String stats(Model model) {
        model.addAttribute("totalStats", gameResultMapper.findTotalStats());
        model.addAttribute("dailyStats", gameResultMapper.findDailyStats());
        model.addAttribute("userStats",  gameResultMapper.findUserStats());
        return "stats";
    }
}