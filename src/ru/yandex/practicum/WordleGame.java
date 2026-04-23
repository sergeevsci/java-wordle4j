package ru.yandex.practicum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WordleGame {

    private String answer;
    private int countSteps;
    private int lengthWord;
    private WordleDictionary dictionary;
    private LogWriter logWriter;
    private GameStatus gameStatus;
    private boolean isUsedHint = false;
    private final GameState gameState;
    private char[] charArrayAnswer;
    private Random random = new Random();
    private ArrayList<String> usedWords = new ArrayList<>(countSteps);
    private ArrayList<String> usedTranscriptsUsedWords = new ArrayList<>(countSteps);

    public WordleGame(int countSteps, int lengthWord, WordleDictionary dictionary,
                    GameStatus gameStatus, LogWriter logWriter) throws GameException {

        this.countSteps = countSteps;
        this.lengthWord = lengthWord;
        this.dictionary = dictionary;
        this.logWriter = logWriter;
        this.gameState = new GameState(logWriter);
        this.gameStatus = gameStatus;

        try {
            answer = generateWord();
        } catch (EmptyDictionaryWordleException e) {
            logWriter.log(e.getMessage(), e);
            gameStatus = GameStatus.EMPTY_DICTIONARY;
        }

        this.charArrayAnswer = answer.toCharArray();
    }


    public String takeStepGame(String input) {

        isUsedHint = false;
        if (input.trim().isEmpty()) {
            isUsedHint = true;
            input = giveHint();
        }

        input = input.trim().toLowerCase().replace('ё', 'е');
        char[] charArrayInput = input.toCharArray();

        usedWords.add(input);
        StringBuilder sbForTranscriptsWord = new StringBuilder();
        String transcriptsWord = checkingForPresenceOfLetter(charArrayInput, charArrayAnswer,
                                                        sbForTranscriptsWord);
        usedTranscriptsUsedWords.add(transcriptsWord);

        gameState.updateFromTranscript(input, transcriptsWord, lengthWord);

        countSteps--;
        readinessCheck(input);
        return transcriptsWord;
    }


    public String giveHint() {

        if (usedWords.isEmpty()) {
            try {
                return generateWord();
            } catch (EmptyDictionaryWordleException e) {
                logWriter.log(e.getMessage(), e);
                gameStatus = GameStatus.EMPTY_DICTIONARY;
                return null;
            }
        }

        List<String> suitableWords = filterWordsByGameState(dictionary.getWords());

        if (suitableWords.isEmpty()) {
            return null;
        }

        int randomIndex = random.nextInt(suitableWords.size());
        return suitableWords.get(randomIndex);
    }


    // стратегия Весов заменена на стратегию явного Искстречающихся букв и удержания точных совпадений
    private List<String> filterWordsByGameState(List<String> words) {

        List<String> result = new ArrayList<>();

        for (String word : words) {
            if (word == null || word.length() != lengthWord || usedWords.contains(word)) {
                continue;
            }

            boolean accept = true;

            // убираем исключенные буквы
            for (char letter : gameState.getExcludedLetters()) {
                if (word.indexOf(letter) != -1) {
                    accept = false;
                    break;
                }
            }

            // + должны быть там где и в ответе
            for (Map.Entry<Integer, Character> entry : gameState.getConfirmedPositions().entrySet()) {
                if (word.charAt(entry.getKey()) != entry.getValue()) {
                    accept = false;
                    break;
                }
            }

            // ^ тоже должны быть в слове для подсказки
            for (char letter : gameState.getPresentLetters()) {
                if (word.indexOf(letter) == -1) {
                    accept = false;
                    break;
                }
            }

            if (accept) {
                result.add(word);
            }
        }

        return result;
    }


    String checkingForPresenceOfLetter(char[] charArrayInput, char[] charArrayAnswer,
                                  StringBuilder stringBuilder) {

        int index = 0;
        for (char letter : charArrayInput) {
            if (containsChar(charArrayAnswer, letter)) {
                if (charArrayAnswer[index] == charArrayInput[index]) {
                    stringBuilder.append('+');
                } else {
                    stringBuilder.append('^');
                }
            } else {
                stringBuilder.append('-');
            }
            index++;
        }

        return stringBuilder.toString();
    }


    String generateWord() throws EmptyDictionaryWordleException {

        if (dictionary.getWords().isEmpty()) {
            throw new EmptyDictionaryWordleException("Словарь для игры в Wordle оказался пуст.");
        }

        return dictionary.getWords().get(random.nextInt(dictionary.getWords().size()));
    }


    static boolean containsChar(char[] array, char target) {

        for (char c : array) {
            if (c == target) {
                return true;
            }
        }

        return false;
    }


    void readinessCheck(String input) {

        if (input.equals(answer)) {
            gameStatus = GameStatus.SUCCESS;
        } else if (countSteps == 0) {
            gameStatus = GameStatus.LOSS;
        }
    }


    ArrayList<String> getUsedWords() {

        return usedWords;
    }


    ArrayList<String> getUsedTranscriptsUsedWords() {

        return usedTranscriptsUsedWords;
    }


    String getAnswer() {

        return answer;
    }


    boolean isUsedHint() {

        return isUsedHint;
    }


    GameStatus getGameStatus() {

        return gameStatus;
    }


    void setGameStatus(GameStatus gameStatus) {

        this.gameStatus = gameStatus;
    }


    WordleDictionary getDictionary() {

        return dictionary;
    }
}
