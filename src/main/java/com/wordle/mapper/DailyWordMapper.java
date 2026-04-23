package com.wordle.mapper;

import com.wordle.model.DailyWord;
import com.wordle.model.User;

import java.time.LocalDate;

public interface DailyWordMapper {
    DailyWord findByPlayDate(LocalDate playDate);
    void      insertDailyWord(DailyWord dailyWord);
}