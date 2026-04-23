package com.wordle.model;

public record Word(String value) {

    // 생성 시 유효성 검증
    public Word {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("단어가 비어있습니다.");
        if (value.length() != 5) throw new IllegalArgumentException("단어는 5글자여야 합니다.");
        value = value.toUpperCase().trim();
    }
}
