package ru.yandex.practicum;
// Добрый день, Сергей! Отправляю на проверку работу)

import java.util.*;

/*
в главном классе нам нужно:
    + создать лог-файл (он должен передаваться во все классы)
    + создать загрузчик словарей WordleDictionaryLoader
    + загрузить словарь WordleDictionary с помощью класса WordleDictionaryLoader
    + затем создать игру WordleGame и передать ей словарь
    + вызвать игровой метод в котором в цикле опрашивать пользователя и передавать информацию в игру
    + вывести состояние игры и конечный результат
 */

public class Wordle {

    private static final String filenameWordsRu = "words_ru.txt";
    private static final String filenameLog = "log.txt";

    private static final int COUNT_STEPS = 6;
    private static final int LENGTH_WORD = 5;


    public static void main(String[] args) {

        LogWriter logWriter = new LogWriter(filenameLog);
        logWriter.clearLogFile();

        try {
            WordleDictionaryLoader wdLoader = new WordleDictionaryLoader(filenameWordsRu, logWriter);
            WordleDictionary wordsDictionary = new WordleDictionary(wdLoader.readWordsFromFile(), logWriter);
            wordsDictionary = wordsDictionary.normalizeWordleDictionary(wordsDictionary.getWords());

            WordleGame wordleGame = new WordleGame(COUNT_STEPS, LENGTH_WORD, wordsDictionary,
                                            GameStatus.READY, logWriter);
            System.out.println("Игра началась. Слово загадано. Отгадывайте.");

            Scanner scanner = new Scanner(System.in);
            while (wordleGame.getGameStatus() == GameStatus.READY) {
                userInput(scanner, logWriter, wordleGame);
            }

            if (wordleGame.getGameStatus() != GameStatus.READY) {
                switch (wordleGame.getGameStatus()) {
                    case SUCCESS -> System.out.println("Да, правильный ответ: "
                            + wordleGame.getAnswer());
                    case LOSS -> System.out.println("Ответ угадать не получилось. Правильное слово: "
                            + wordleGame.getAnswer());
                }
            }
        } catch (GameException e) {
            System.out.println("Ошибка программы: " + e.getMessage());
        } catch (Exception e) {
            logWriter.log("Неожиданная ошибка: ", e);
        }
    }


    static void userInput(Scanner scanner, LogWriter logWriter, WordleGame wordleGame) {

        String input;
        try {
            input = scanner.nextLine();
            if ((input.length() != LENGTH_WORD) && (!input.isBlank())) {
                throw new WordIsNot5CharactersLong("Вы ввели слово состоящее не из "
                        + LENGTH_WORD + " символов.");
            }

            if (input.equals(wordleGame.getAnswer())) {
                wordleGame.setGameStatus(GameStatus.SUCCESS);
                return;
            }

            if ((!wordleGame.getDictionary().getWords().contains(input.trim().toLowerCase()))
                    && (!input.trim().isEmpty())) {
                throw new EnteredWordIsNotInListAvailableWords(
                        "Введенного слова нет в списке слов доступных к вводу.");
            }

            String step = wordleGame.takeStepGame(input);
            if (wordleGame.isUsedHint()) {
                System.out.printf("%s%n", wordleGame.getUsedWords().getLast());
            }
            System.out.printf("%s%n", step);

        } catch (NoSuchElementException | IllegalStateException e) {
            System.out.println("Произошла ошибка. Попробуйте еще раз.");
            logWriter.log(e.getMessage(), e);
        } catch (WordIsNot5CharactersLong | EnteredWordIsNotInListAvailableWords e) {
            System.out.println("Ошибка ввода." + e.getMessage());
        }
    }
}
