package ru.yandex.practicum;

public class GameUnexpectedError extends RuntimeException {

    public GameUnexpectedError(String message) {

        super(message);
    }
}
