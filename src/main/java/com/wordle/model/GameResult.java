package com.wordle.model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameResult {
    private Long          id;
    private Long          userId;
    private LocalDate     playDate;
    private int           tryCount;
    private boolean       win;
    private LocalDateTime successAt;
}