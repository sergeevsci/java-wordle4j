package ru.yandex.practicum;

/*
в главном классе нам нужно:
    создать лог-файл (он должен передаваться во все классы)
    создать загрузчик словарей WordleDictionaryLoader
    загрузить словарь WordleDictionary с помощью класса WordleDictionaryLoader
    затем создать игру WordleGame и передать ей словарь
    вызвать игровой метод в котором в цикле опрашивать пользователя и передавать информацию в игру
    вывести состояние игры и конечный результат
 */
public class Wordle {

    private static final String filenameWordsRu = "words_ru.txt";
    private static final String filenameLog = "log.txt";

    public static void main(String[] args) {

        LogWriter logWriter = new LogWriter(filenameLog);
        try {
            WordleDictionaryLoader wdLoader = new WordleDictionaryLoader(filenameWordsRu, logWriter);
            WordleDictionary wordsDictionary = new WordleDictionary(wdLoader.readWordsFromFile(), logWriter);
            wordsDictionary = wordsDictionary.normalizeWordleDictionary(wordsDictionary.getWords());
            //System.out.println(wordsDictionary.getWords()); // окей, слова, очищенные по правилам получили
            WordleGame wordleGame = new WordleGame("сгенерированное слово", 6, wordsDictionary, logWriter);
            wordleGame.start();
        }
        /*
        catch (GameException e) { // нужен в блоке try метод который будет throws GameException
            System.out.println("Ошибка программы: " + e.getMessage()); // не логгируем игровые ошибки.
        }
        */
        catch (Exception e) {
            logWriter.log("Неожиданная ошибка: ", e); // все остальные в файл
        }

    }

}
