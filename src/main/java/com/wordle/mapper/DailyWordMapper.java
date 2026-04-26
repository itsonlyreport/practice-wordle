package com.wordle.mapper;

import com.wordle.model.DailyWord;

import java.time.LocalDate;
import java.util.List;

public interface DailyWordMapper {
    DailyWord findByPlayDate(LocalDate playDate);
    void      insertDailyWord(DailyWord dailyWord);
    List<String> findAllWords();   // ← 기존 단어 목록 조회
}