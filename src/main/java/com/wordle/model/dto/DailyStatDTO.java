package com.wordle.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyStatDTO {
    private LocalDate playDate;
    private int totalCount; // 총 플레이 수
    private int winCount; // 성공 수
    private int loseCount; // 실패 수
    private double winRate; // 성공률
    private double avgTryCount; // 평균 시도 횟수
}