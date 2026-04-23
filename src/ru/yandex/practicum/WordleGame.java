package ru.yandex.practicum;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
    private ArrayList<String> usedWords = new ArrayList<>(countSteps); // usedWords используется в важных местах
    /*
     1. add word to usedWords
     2. для проверки первой подсказки: Если слов не было - подсказка юзается первый раз, следовательно, не должна быть ответом
     3. для вычеркивания из списка возможных подсказок: Если слово уже было (введено или сгенерировано) - его не добавляем
                в список возможных слов для генерации подсказки
     4. getter...
     5. этот геттер из 4. используется для вывода в консоль сгенерированной подсказки

     Мб я не прав и можно сделать все проще?
     */
    // а вот шифры usedWords действительно не используются нигде - их убрал

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

        if (input.isBlank()) {
            isUsedHint = true;
            input = giveHint();
            if (input == null) {
                return null; // вот тут null не нужен. лучше throw new
            }
        }

        char[] charArrayInput = input.toCharArray();

        usedWords.add(input);
        StringBuilder sbForTranscriptsWord = new StringBuilder();
        String transcriptsWord = checkingForPresenceOfLetter(charArrayInput, charArrayAnswer,
                                                        sbForTranscriptsWord);

        // Обновляем gameState для следующей подсказки
        gameState.updateFromTranscript(input, transcriptsWord, lengthWord);

        if (transcriptsWord.equals("+++++")) {
            gameStatus = GameStatus.SUCCESS;
            return transcriptsWord;
        }

        countSteps--;
        readinessCheck(input);
        return transcriptsWord;
    }


    public String giveHint() {

        if (usedWords.isEmpty()) { // Если слов не было - подсказка юзается первый раз, следовательно, не должна быть ответом
            try {
                return generateWord(candidate -> !candidate.equals(answer));
            } catch (EmptyDictionaryWordleException e) {
                logWriter.log(e.getMessage(), e);
                gameStatus = GameStatus.EMPTY_DICTIONARY;
                return null;
            }
        }

        List<String> suitableWords = filterWordsByGameState(dictionary.getWords());

        if (suitableWords.isEmpty()) {
            // если словарь подсказок пустой - выведем любое слово - но это крайние меры
            try {
                return generateWord();
            } catch (EmptyDictionaryWordleException e) {
                logWriter.log(e.getMessage(), e);
                gameStatus = GameStatus.EMPTY_DICTIONARY;
                return null;
            }

        }

        int randomIndex = random.nextInt(suitableWords.size());
        return suitableWords.get(randomIndex);
    }


    private List<String> filterWordsByGameState(List<String> words) {

        List<String> result = new ArrayList<>();

        Set<Character> excluded = gameState.getExcludedLetters();
        Map<Integer, Character> confirmed = gameState.getConfirmedPositions();
        Set<Character> present = gameState.getPresentLetters();

        for (String word : words) {
            if (word == null || word.length() != lengthWord || usedWords.contains(word)) {
                continue;
            }

            boolean accept = true;

            for (char letter : excluded) {
                if (word.indexOf(letter) != -1) {
                    accept = false;
                    break;
                }
            }
            if (!accept) continue;

            for (Map.Entry<Integer, Character> entry : confirmed.entrySet()) {
                if (word.charAt(entry.getKey()) != entry.getValue()) {
                    accept = false;
                    break;
                }
            }
            if (!accept) continue;

            for (char letter : present) {
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


    String checkingForPresenceOfLetter(char[] charArrayInput, char[] charArrayAnswer, StringBuilder stringBuilder) {

        Map<Character, Integer> availableLetters = new HashMap<>();
        for (char c : charArrayAnswer) {
            availableLetters.put(c, availableLetters.getOrDefault(c, 0) + 1);
        }

        int index = 0;
        for (char letter : charArrayInput) {
            if (availableLetters.containsKey(letter) && availableLetters.get(letter) > 0) {
                if (charArrayAnswer[index] == letter) {
                    // Точное совпадение на позиции
                    stringBuilder.append('+');
                    availableLetters.put(letter, availableLetters.get(letter) - 1);
                } else {
                    // Буква есть, но не на этой позиции
                    stringBuilder.append('^');
                    availableLetters.put(letter, availableLetters.get(letter) - 1);
                }
            } else {
                // Буквы нет в ответе или все вхождения уже использованы
                stringBuilder.append('-');
            }
            index++;
        }
        return stringBuilder.toString();

    }


    public String generateWord() throws EmptyDictionaryWordleException {

        if (dictionary.getWords().isEmpty()) {
            throw new EmptyDictionaryWordleException("Словарь для игры в Wordle оказался пуст.");
        }

        return dictionary.getWords().get(random.nextInt(dictionary.getWords().size()));
    }

    public String generateWord(Predicate<String> validator) throws EmptyDictionaryWordleException {
        if (dictionary.getWords().isEmpty()) {
            throw new EmptyDictionaryWordleException("Словарь для игры в Wordle оказался пуст.");
        }

        List<String> validWords = dictionary.getWords().stream()
                .filter(validator)
                .collect(Collectors.toList());

        if (validWords.isEmpty()) {
            throw new EmptyDictionaryWordleException("Нет слов, удовлетворяющих условию валидатора (candidate != answer).");
        }

        return validWords.get(random.nextInt(validWords.size()));
    }


    public static boolean containsChar(char[] array, char target) {

        for (char c : array) {
            if (c == target) {
                return true;
            }
        }

        return false;
    }


    public static int countCharInWord(char[] array, char target) {
        int count = 0;
        for (char c : array) {
            if (c == target) {
                count++;
            }
        }
        return count;
    }


    public void readinessCheck(String input) {

        if (input.equals(answer)) {
            gameStatus = GameStatus.SUCCESS;
        } else if (countSteps == 0) {
            gameStatus = GameStatus.LOSS;
        }
    }


    public ArrayList<String> getUsedWords() {

        return usedWords;
    }


    public String getAnswer() {

        return answer;
    }


    public boolean isUsedHint() {

        return isUsedHint;
    }


    public GameStatus getGameStatus() {

        return gameStatus;
    }


    public void setGameStatus(GameStatus gameStatus) {

        this.gameStatus = gameStatus;
    }


    public WordleDictionary getDictionary() {

        return dictionary;
    }
}
