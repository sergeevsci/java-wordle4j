package ru.yandex.practicum;

public class EnteredWordIsNotInListAvailableWords extends GameRuleException {

    public EnteredWordIsNotInListAvailableWords(String message) {

        super(message);
    }
}
