package com.wordle.model;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyWord {
    private Long      id;
    private String    word;
    private LocalDate playDate;
}
