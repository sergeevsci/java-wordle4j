package ru.yandex.practicum;

import java.util.Random;
import java.util.List;
import java.util.Arrays;

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

    public WordleGame(int steps, WordleDictionary dictionary, LogWriter logWriter) throws GameException {
        this.steps = steps;
        this.dictionary = dictionary;
        this.logWriter = logWriter;
    }

    GameStatus startGame() {
        try {
            this.answer = generateAnswerWord(); // Выберем случайное слово из нашего списка
        } catch (EmptyDictionaryWordleException e) {
            logWriter.log(e.getMessage(), e);
            return GameStatus.EMPTY_DICTIONARY;
        }
        this.gameStatus = GameStatus.READY;
        return this.gameStatus;
    }

    GameStatus takeStepGame(String step) {

        return GameStatus.READY; // или (SUCCESS или LOSS).
    }

    String getAnswer() {
        return answer;
    }

    int getSteps() {
        return steps;
    }

    GameStatus getGameStatus() {
        return gameStatus;
    }

    String generateAnswerWord() throws EmptyDictionaryWordleException {
        Random random = new Random();
        if (dictionary.getWords().isEmpty()) {
            throw new EmptyDictionaryWordleException("Словарь для игры в Wordle оказался пуст."); // null-значение по умолчанию
        }
        return dictionary.getWords().get(random.nextInt(dictionary.getWords().size()));
    }


}
