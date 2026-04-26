package com.wordle.mapper;

import com.wordle.model.DailyWord;

import java.time.LocalDate;
import java.util.List;

public interface DailyWordMapper {
    DailyWord findByPlayDate(LocalDate playDate);
    DailyWord findById(Long id);
    void insertDailyWord(DailyWord dailyWord);
    List<String> findAllWords();
    List<DailyWord> findAllOrderByDate();
    void updateDailyWord(DailyWord dailyWord);
    void deleteDailyWord(Long id);
}