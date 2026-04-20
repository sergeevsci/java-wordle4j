package ru.yandex.practicum;

import java.util.*;

class GameState {
    private final Set<Character> excludedLetters = new HashSet<>();
    private final Map<Integer, Character> confirmedPositions = new HashMap<>();
    private final Set<Character> presentLetters = new HashSet<>();

    public void updateFromTranscript(String word, String transcript) {
        for (int i = 0; i < 5; i++) {
            char symbol = transcript.charAt(i);
            char letter = word.charAt(i);

            switch (symbol) {
                case '-' -> excludedLetters.add(letter);
                case '+' -> confirmedPositions.put(i, letter);
                case '^' -> presentLetters.add(letter);
            }
        }
    }

    public Set<Character> getExcludedLetters() {
        return excludedLetters;
    }
    public Map<Integer, Character> getConfirmedPositions() {
        return confirmedPositions;
    }
    public Set<Character> getPresentLetters() {
        return presentLetters;
    }
}