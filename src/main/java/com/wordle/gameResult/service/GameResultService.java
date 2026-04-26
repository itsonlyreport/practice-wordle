package com.wordle.gameResult.service;

import com.wordle.mapper.GameResultMapper;
import com.wordle.model.GameResult;
import com.wordle.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class GameResultService {

    @Autowired
    private GameResultMapper gameResultMapper;

    // 로그인 유저만 DB 저장
    public void save(User loginUser, int tryCount, boolean isWin) {
        if (isGuest(loginUser)) return;  // GUEST는 저장 안함

        var result = GameResult.builder()
                .userId(loginUser.getId())
                .playDate(LocalDate.now())
                .tryCount(tryCount)
                .win(isWin)
                .successAt(isWin ? LocalDateTime.now() : null)
                .build();

        gameResultMapper.insertGameResult(result);
    }

    private boolean isGuest(User user) {
        return "GUEST".equals(user.getLoginType());
    }
}