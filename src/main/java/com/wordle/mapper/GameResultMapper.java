package com.wordle.mapper;

import com.wordle.model.GameResult;
import com.wordle.model.dto.DailyStatDTO;
import com.wordle.model.dto.UserStatDTO;

import java.util.List;

public interface GameResultMapper {
    void insertGameResult(GameResult result);
    List<DailyStatDTO> findDailyStats(); // 날짜별 통계
    List<UserStatDTO> findUserStats(); // 유저별 전적
    DailyStatDTO findTotalStats(); // 전체 통계
}