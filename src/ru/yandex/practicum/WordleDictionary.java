package ru.yandex.practicum;

import java.util.ArrayList;
import java.util.List;

/*
этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
 */

public class WordleDictionary {

    private List<String> words;
    LogWriter logWriter;


    public WordleDictionary(List<String> words, LogWriter logWriter) {

        this.words = words;
        this.logWriter = logWriter;
    }


    public List<String> getWords() {

        return words;
    }


    WordleDictionary normalizeWordleDictionary(List<String> rwFromFile) {

        List<String> normalizeWords = new ArrayList<>();
        for (String str : rwFromFile) {
            if (str.length() == 5) {
                str = str.toLowerCase();
                if (str.indexOf('ё') != -1) {
                    normalizeWords.add(str.replaceAll("ё", "e"));
                } else {
                    normalizeWords.add(str);
                }
            }
        }

        return new WordleDictionary(normalizeWords, this.logWriter);
    }
}
