package ru.yandex.practicum;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
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

        List<String> str = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(this.filename),
                        StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                str.add(line);
            }
        } catch (IOException e) {
            logWriter.log("Неожиданная ошибка: ", e);
        }

        return str;
    }
}
