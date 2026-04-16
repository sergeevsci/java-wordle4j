package ru.yandex.practicum;

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

    public WordleGame(String answer, int steps, WordleDictionary dictionary, LogWriter logWriter) {
        this.answer = answer;
        this.steps = steps;
        this.dictionary = dictionary;
        this.logWriter = logWriter;
    }

    int start() {
        System.out.println("Игра началась");
        return 0;
    }

}
