package ru.yandex.practicum;

import java.util.Scanner;

/*
в главном классе нам нужно:
    + создать лог-файл (он должен передаваться во все классы)
    + создать загрузчик словарей WordleDictionaryLoader
    + загрузить словарь WordleDictionary с помощью класса WordleDictionaryLoader
    + затем создать игру WordleGame и передать ей словарь
    ! вызвать игровой метод в котором в цикле опрашивать пользователя и передавать информацию в игру
    ! вывести состояние игры и конечный результат
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
            WordleGame wordleGame = new WordleGame(6, wordsDictionary, logWriter);
            System.out.println("Игра началась. Слово загадано. Отгадывайте.");

            Scanner scanner = new Scanner(System.in);
            while (wordleGame.getGameStatus() == GameStatus.READY) {
                String input = scanner.nextLine();
                // сразу провели обработку введенного. Обвести в try. и методу takeStepGame(String input);
            }

            if (wordleGame.getGameStatus() != GameStatus.READY) { // Конец игры
                switch (wordleGame.getGameStatus()) {
                    case GameStatus.SUCCESS -> System.out.println("Да, правильный ответ: " + wordleGame.getAnswer());
                    case GameStatus.LOSS -> System.out.println("Ответ угадать не получилось. Правильное слово: " + wordleGame.getAnswer());
                }
            }
        }
        catch (GameException e) {
            System.out.println("Ошибка программы: " + e.getMessage()); // не логгируем игровые ошибки.
        }
        catch (Exception e) {
            logWriter.log("Неожиданная ошибка: ", e); // все остальные в файл
        }

    }

}
