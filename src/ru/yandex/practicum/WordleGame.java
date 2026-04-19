package ru.yandex.practicum;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
в этом классе хранится словарь и состояние игры
    текущий шаг
    всё что пользователь вводил
    правильный ответ

в этом классе нужны методы, которые
    проанализируют совпадение слова с ответом
    предложат слово-подсказку с учётом всего, что вводил пользователь ранее

не забудьте про специальные типы исключений для игровых и неигровых ошибок
 */
public class WordleGame {

    private String answer;

    private int steps;

    private WordleDictionary dictionary;

    LogWriter logWriter;

    private GameStatus gameStatus;

    char[] charArrayAnswer;

    private ArrayList<String> usedWords = new ArrayList<>(6);
    private ArrayList<String> usedTranscriptsUsedWords = new ArrayList<>(6);

    public WordleGame(int steps, WordleDictionary dictionary, GameStatus gameStatus, LogWriter logWriter) throws GameException {
        this.steps = steps;
        this.dictionary = dictionary;
        this.logWriter = logWriter;
        this.gameStatus = gameStatus;
        try {
            answer = generateWord(); // Выберем случайное слово из нашего списка
        } catch (EmptyDictionaryWordleException e) {
            logWriter.log(e.getMessage(), e);
            gameStatus = GameStatus.EMPTY_DICTIONARY;
        }
        this.charArrayAnswer = answer.toCharArray();
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public WordleDictionary getDictionary() {
        return dictionary;
    }

    String takeStepGame(String input) { // Делаем шаг
        boolean isUseHint = false;
        if (input.trim().isEmpty() || input.isEmpty()) {
            isUseHint = true;
            input = giveHint(charArrayAnswer); // активируем подсказку при пустом вводе
        }

        StringBuilder stringBuilder = new StringBuilder();
        if (isUseHint) {
            stringBuilder.append(input);
            stringBuilder.append(" ");
        }
        input = input.trim().toLowerCase();
        input = input.replace('ё', 'е');

        char[] charArrayInput = input.toCharArray();

        usedWords.add(input);
        String transcriptsWord = checkingForPresenceOfLetter(charArrayInput, charArrayAnswer, stringBuilder);
        usedTranscriptsUsedWords.add(transcriptsWord);
        steps = steps-1; // Шаг потратили
        readinessCheck();
        return transcriptsWord;
    }

    String giveHint(char[] charArrayAnswer) { // Даем подсказку при пустом вводе. !! Игру можно пройти одними подсказками
        if (usedWords.isEmpty()) { // Пользователь сразу просит подсказку. Можно любое слово впихнуть.
            String hint = "";
            try {
                hint = generateWord(); // Выберем случайное слово из нашего списка
            } catch (EmptyDictionaryWordleException e) {
                logWriter.log(e.getMessage(), e);
                gameStatus = GameStatus.EMPTY_DICTIONARY;
            }
            return hint;
        }

        int[] weights = calculateAnswerWordWeights();

        // Находим букву с наименьшим весом
        int minWeight = Integer.MAX_VALUE;
        int hintPosition = -1;

        for (int i = 0; i < weights.length; i++) {
            if (weights[i] < minWeight) {
                minWeight = weights[i];
                hintPosition = i;
            }
        }

        char hintLetter = ' ';
        if (hintPosition != -1) {
            hintLetter = answer.charAt(hintPosition);
            /*
            System.out.println("Подсказка: буква '" + hintLetter +
                    "' на позиции " + (hintPosition + 1) +
                    " (вес: " + minWeight + ")");

             */
        }

        return generateHintWord(hintLetter, hintPosition);
    }

    String generateHintWord(char hintLetter, int hintPosition) {
        // Далее можно использовать hintLetter и hintPosition
        // для подбора слова, где эта буква стоит на нужной позиции
        return "чувак";
    }

    public int[] calculateAnswerWordWeights() {
        int[] answerLetterWeight = new int[5];
        for (int i = 0; i < usedTranscriptsUsedWords.size(); i++) { // беру шифр каждого введенного ответа и смотрю че это за символ в том слове был. и нас этот символ вес плюсуется
            String transcriptsWord = usedTranscriptsUsedWords.get(i);

            for (int j = 0; j < transcriptsWord.length(); j++) {
                char symbol = transcriptsWord.charAt(j);
                int weight = 0;

                switch (symbol) { // Определяем вес символа
                    case '+':
                        weight = 100;
                        break;
                    case '-':
                        weight = 10;
                        break;
                    case '^':
                        weight = 1;
                        break;
                    default:
                        weight = 0; // игнорируем другие символы текущего шифра
                }
                // Добавляем вес к соответствующей позиции буквы ответа
                answerLetterWeight[j] += weight;
            }
        }
        return answerLetterWeight;
    }

    String checkingForPresenceOfLetter(char[] charArrayInput, char[] charArrayAnswer, StringBuilder stringBuilder) {
        int index = 0;
        for (char letter : charArrayInput) {
            if (containsChar(charArrayAnswer, letter)) { // Если эта буква из введенного слова есть в ответе
                if (charArrayAnswer[index] == charArrayInput[index]) { // осталось узнать на каком месте она есть
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

    void readinessCheck() { // Проверка готовности продолжать игру
        if (steps == 0) {
            gameStatus = GameStatus.LOSS;
        }
    }

    ArrayList<String> getUsedWords() {
        return usedWords;
    }

    ArrayList<String> getUsedTranscriptsUsedWords() {
        return usedTranscriptsUsedWords;
    }

    public static boolean containsChar(char[] array, char target) {
        for (char c : array) {
            if (c == target) {
                return true;
            }
        }
        return false;
    }

    String getAnswer() {
        return answer;
    }

    GameStatus getGameStatus() {
        return gameStatus;
    }

    String generateWord() throws EmptyDictionaryWordleException {
        Random random = new Random();
        if (dictionary.getWords().isEmpty()) {
            throw new EmptyDictionaryWordleException("Словарь для игры в Wordle оказался пуст."); // null-значение по умолчанию
        }
        return dictionary.getWords().get(random.nextInt(dictionary.getWords().size()));
    }

}
