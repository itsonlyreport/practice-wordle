package com.wordle.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WordleGame implements Serializable {

    private static final int MAX_TRIES = 6;

    private final Word answer;
    private final List<String> guesses = new ArrayList<>();
    private final List<int[]>  results = new ArrayList<>();
    private boolean gameOver;
    private boolean win;

    public WordleGame(Word answer) {
        this.answer = answer;
    }

    public String submitGuess(String raw) {
        var guess = raw.toUpperCase().trim();

        if (gameOver)             return "게임이 이미 끝났습니다.";
        if (guess.length() != 5) return "5글자를 입력해주세요.";

        var result = evaluate(guess);
        guesses.add(guess);
        results.add(result);

        if (guess.equals(answer.value())) {
            gameOver = true;
            win      = true;
        } else if (guesses.size() >= MAX_TRIES) {
            gameOver = true;
        }

        return null;
    }

    private int[] evaluate(String guess) {
        var result     = new int[5];
        var answerUsed = new boolean[5];
        var guessUsed  = new boolean[5];

        // 1pass: 정확한 위치 (초록)
        for (int i = 0; i < 5; i++) {
            if (guess.charAt(i) == answer.value().charAt(i)) {
                result[i] = 2;
                answerUsed[i] = guessUsed[i] = true;
            }
        }

        // 2pass: 있지만 위치 틀림 (노랑)
        for (int i = 0; i < 5; i++) {
            if (guessUsed[i]) continue;
            for (int j = 0; j < 5; j++) {
                if (!answerUsed[j] && guess.charAt(i) == answer.value().charAt(j)) {
                    result[i]     = 1;
                    answerUsed[j] = true;
                    break;
                }
            }
        }

        return result;
    }

    public Word getAnswer()          { return answer; }
    public List<String> getGuesses() { return guesses; }
    public List<int[]> getResults()  { return results; }
    public boolean isGameOver()      { return gameOver; }
    public boolean isWin()           { return win; }
    public int getMaxTries()         { return MAX_TRIES; }
}