package ru.yandex.practicum;

public enum GameStatus {
        SUCCESS(0, "Игра успешно завершена"),
        UNKNOWN_ERROR(-1, "Неизвестная ошибка"),
        EMPTY_DICTIONARY(-2, "Ошибка. Словарь слов для игры пуст"),
        READY(1, "Игра ожидает вариант ответа."),
        LOSS(-3, "Проигрыш. Слово не удалось отгадать.");


        private final int code;
        private final String message;

        GameStatus(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() { return code; }
        public String getMessage() { return message; }
}
