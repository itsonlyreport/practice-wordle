package com.wordle.mapper;

import com.wordle.model.GameResult;

public interface GameResultMapper {
    void insertGameResult(GameResult result);
}