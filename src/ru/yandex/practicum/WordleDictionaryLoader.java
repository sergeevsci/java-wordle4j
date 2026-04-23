package ru.yandex.practicum;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/*
этот класс содержит в себе всю рутину по работе с файлами словарей и с кодировками
    ему нужны методы по загрузке списка слов из файла по имени файла
    на выходе должен быть класс WordleDictionary
 */

public class WordleDictionaryLoader {

    private final String filename;
    LogWriter logWriter;


    public WordleDictionaryLoader(String filename, LogWriter logWriter) {

        this.filename = filename;
        this.logWriter = logWriter;
    }


    public List<String> readWordsFromFile() {

        if (this.filename == null || this.filename.trim().isEmpty()) {
            throw new TheDictionaryWasNotLoaded("Имя файла словаря не указано. Игра остановлена.");
        }

        List<String> str = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(this.filename),
                        StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                str.add(line);
            }
        } catch (FileNotFoundException e) {
            logWriter.log("Файл не найден: " + this.filename, e);
            throw new TheDictionaryWasNotLoaded("Файл словаря '" + this.filename + "' не найден. Игра остановлена.");

        } catch (SecurityException e) {
            logWriter.log("Нет доступа к файлу: " + this.filename, e);
            throw new TheDictionaryWasNotLoaded("Нет прав доступа к файлу словаря. Игра остановлена.");

        } catch (IOException e) {
            logWriter.log("Ошибка чтения файла '" + this.filename + "': " + e.getMessage(), e);
            throw new TheDictionaryWasNotLoaded("Ошибка чтения словаря. Игра остановлена.");
        }

        return str;
    }
}
