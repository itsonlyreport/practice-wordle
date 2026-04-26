package com.wordle.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserStatDTO {
    private Long   userId;
    private String username;
    private String email;
    private String loginType;
    private int    totalCount;   // 총 플레이 수
    private int    winCount;     // 성공 수
    private double winRate;      // 성공률
    private double avgTryCount;  // 평균 시도 횟수
}