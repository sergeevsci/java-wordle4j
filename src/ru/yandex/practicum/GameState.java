package ru.yandex.practicum;

import java.util.*;

class GameState {

    private final Set<Character> excludedLetters = new HashSet<>();
    private final Map<Integer, Character> confirmedPositions = new HashMap<>();
    private final Set<Character> presentLetters = new HashSet<>();
    private final Map<Character, Integer> maxLetterOccurrences = new HashMap<>();
    private final LogWriter logWriter;


    public GameState(LogWriter logWriter) {

        this.logWriter = logWriter;
    }


    public void updateFromTranscript(String word, String transcript, int lengthWord) {
        for (int i = 0; i < lengthWord; i++) {
            char symbol = transcript.charAt(i);
            char letter = word.charAt(i);

            switch (symbol) {
                case '-' -> excludedLetters.add(letter);
                case '+' -> {
                    confirmedPositions.put(i, letter);
                    // Фиксируем максимальное количество вхождений
                    maxLetterOccurrences.put(
                            letter,
                            Math.max(
                                    maxLetterOccurrences.getOrDefault(letter, 0),
                                    countOccurrencesInWord(word, letter, i + 1)
                            )
                    );
                }
                case '^' -> {
                    presentLetters.add(letter);
                    // Аналогично для ^
                    maxLetterOccurrences.put(
                            letter,
                            Math.max(
                                    maxLetterOccurrences.getOrDefault(letter, 0),
                                    countOccurrencesInWord(word, letter, i + 1)
                            )
                    );
                }
            }
        }
    }


    private int countOccurrencesInWord(String word, char target, int maxPosition) {
        int count = 0;
        for (int i = 0; i < Math.min(maxPosition, word.length()); i++) {
            if (word.charAt(i) == target) {
                count++;
            }
        }
        return count;
    }

    public Map<Character, Integer> getMaxLetterOccurrences() {
        return maxLetterOccurrences;
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