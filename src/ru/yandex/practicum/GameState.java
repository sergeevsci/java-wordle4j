package ru.yandex.practicum;

import java.util.*;

class GameState {

    private final Set<Character> excludedLetters = new HashSet<>();
    private final Map<Integer, Character> confirmedPositions = new HashMap<>();
    private final Set<Character> presentLetters = new HashSet<>();
    private final Map<Character, Integer> minLetterOccurrences = new HashMap<>();
    private final Map<Character, Integer> maxLetterOccurrences = new HashMap<>();
    private final Map<Character, Set<Integer>> forbiddenPositions = new HashMap<>();
    private final LogWriter logWriter;


    public GameState(LogWriter logWriter) {

        this.logWriter = logWriter;
    }


    public void updateFromTranscript(String word, String transcript, int lengthWord) {
        Map<Character, Integer> positiveInGuess = new HashMap<>();
        Set<Character> minusInGuess = new HashSet<>();

        for (int i = 0; i < lengthWord; i++) {
            char symbol = transcript.charAt(i);
            char letter = word.charAt(i);

            switch (symbol) {
                case '-' -> minusInGuess.add(letter);
                case '+' -> {
                    confirmedPositions.put(i, letter);
                    presentLetters.add(letter);
                    positiveInGuess.put(letter, positiveInGuess.getOrDefault(letter, 0) + 1);
                }
                case '^' -> {
                    presentLetters.add(letter);
                    forbiddenPositions
                            .computeIfAbsent(letter, key -> new HashSet<>())
                            .add(i);
                    positiveInGuess.put(letter, positiveInGuess.getOrDefault(letter, 0) + 1);
                }
                default -> {
                }
            }
        }

        for (Map.Entry<Character, Integer> entry : positiveInGuess.entrySet()) {
            char letter = entry.getKey();
            int positives = entry.getValue();
            int currentMin = minLetterOccurrences.getOrDefault(letter, 0);
            minLetterOccurrences.put(letter, Math.max(currentMin, positives));
            excludedLetters.remove(letter);
        }

        for (char letter : minusInGuess) {
            int positives = positiveInGuess.getOrDefault(letter, 0);

            if (positives == 0) {
                if (!presentLetters.contains(letter)
                        && !confirmedPositions.containsValue(letter)
                        && minLetterOccurrences.getOrDefault(letter, 0) == 0) {
                    excludedLetters.add(letter);
                    maxLetterOccurrences.put(letter, 0);
                }
            } else {
                int minAllowed = minLetterOccurrences.getOrDefault(letter, positives);
                int maxAllowed = Math.max(minAllowed, positives);
                int currentMax = maxLetterOccurrences.getOrDefault(letter, Integer.MAX_VALUE);
                maxLetterOccurrences.put(letter, Math.min(currentMax, maxAllowed));
            }
        }
    }

    public Map<Character, Integer> getMinLetterOccurrences() {
        return minLetterOccurrences;
    }

    public Map<Character, Integer> getMaxLetterOccurrences() {
        return maxLetterOccurrences;
    }

    public Map<Character, Set<Integer>> getForbiddenPositions() {
        return forbiddenPositions;
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
