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
    private PrintWriter printWriter;

    public LogWriter(String filename) {
        this.filename = filename;
    }

    public LogWriter(PrintWriter printWriter) {
        this.filename = null;
        this.printWriter = printWriter;
    }

    public void log(String message, Exception exception) {
        if (printWriter != null) {
            printWriter.println(dateFormat.format(new Date()));
            printWriter.println("Сообщение: " + message);
            printWriter.println("Исключение: " + exception.getClass().getSimpleName() +
                    " - " + exception.getMessage());
            exception.printStackTrace(printWriter);
            printWriter.println("--- Конец записи ---");
            printWriter.println();
            return;
        }

        try (FileWriter fileWriter = new FileWriter(filename, true);
             PrintWriter pw = new PrintWriter(fileWriter)) {

            pw.println(dateFormat.format(new Date()));
            pw.println("Сообщение: " + message);

            pw.println("Исключение: " + exception.getClass().getSimpleName() +
                    " - " + exception.getMessage());

            pw.println("Трассировка стека:");
            exception.printStackTrace(pw);
            pw.println("--- Конец записи ---");
            pw.println();
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
