package ru.yandex.practicum;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.text.SimpleDateFormat;

public class LogWriter {

    private final String filename;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public LogWriter(String filename) {
        this.filename = filename;
    }

    public void log(String message, Exception exception) {
        try (FileWriter fileWriter = new FileWriter(filename, true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {

            // Записываем временную метку и сообщение
            printWriter.println(dateFormat.format(new Date()));
            printWriter.println("Сообщение: " + message);

            // Записываем тип и сообщение исключения
            printWriter.println("Исключение: " + exception.getClass().getSimpleName() +
                    " - " + exception.getMessage());

            // Полная трассировка стека
            printWriter.println("Трассировка стека:");
            exception.printStackTrace(printWriter);
            printWriter.println("--- Конец записи ---");
            printWriter.println(); // пустая строка для разделения записей

        } catch (IOException ioe) {
            System.err.println("КРИТИЧЕСКАЯ ОШИБКА: не удалось записать в лог-файл: " + ioe.getMessage());
            System.err.println("Исходная ошибка: " + message);
        }
    }

    public void clearLogFile() {
        Path logPath = Paths.get("log.txt");
        try {
            Files.deleteIfExists(logPath);
        } catch (Exception e) {
            System.err.println("Ошибка при удалении файла: " + e.getMessage());
        }
    }

}
