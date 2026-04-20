package ru.yandex.practicum;

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

    private static final int countSteps = 6;
    private static final int lengthWord = 5;


    public static void main(String[] args) {

        LogWriter logWriter = new LogWriter(filenameLog);
        logWriter.clearLogFile(); // удалили файл если он был до запуска
        try {
            WordleDictionaryLoader wdLoader = new WordleDictionaryLoader(filenameWordsRu, logWriter);
            WordleDictionary wordsDictionary = new WordleDictionary(wdLoader.readWordsFromFile(), logWriter);
            wordsDictionary = wordsDictionary.normalizeWordleDictionary(wordsDictionary.getWords());
            //System.out.println(wordsDictionary.getWords()); // окей, слова, очищенные по правилам получили
            WordleGame wordleGame = new WordleGame(countSteps, lengthWord, wordsDictionary, GameStatus.READY, logWriter);
            System.out.println("Игра началась. Слово загадано. Отгадывайте.");

            Scanner scanner = new Scanner(System.in);
            while (wordleGame.getGameStatus() == GameStatus.READY) {
                userInput(scanner, logWriter, wordleGame);
            }

            if (wordleGame.getGameStatus() != GameStatus.READY) { // Конец игры
                switch (wordleGame.getGameStatus()) {
                    case GameStatus.SUCCESS -> System.out.println("Да, правильный ответ: " + wordleGame.getAnswer());
                    /*
                    case GameStatus.SUCCESS -> System.out.println("Да, правильный ответ: " + wordleGame.getAnswer() + wordleGame.getUsedWords()
                            + wordleGame.getUsedTranscriptsUsedWords());
                     */
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

    static void userInput(Scanner scanner, LogWriter logWriter, WordleGame wordleGame) {
        String input;
        try { // сразу провели обработку введенного. Обвести в try. и методу takeStepGame(String input); +
            input = scanner.nextLine();
            if ((input.length() != lengthWord) && (!input.trim().isEmpty())) {
                throw new WordIsNot5CharactersLong("Вы ввели слово состоящее не из "+ lengthWord +" символов.");
            }

            if (input.equals(wordleGame.getAnswer())) {
                wordleGame.setGameStatus(GameStatus.SUCCESS);
                return;
            }

            if ((!wordleGame.getDictionary().getWords().contains(input.trim().toLowerCase())) && (!input.trim().isEmpty())) {
                throw new EnteredWordIsNotInListAvailableWords("Введенного слова нет в списке слов доступных к вводу.");
            }

            String step = wordleGame.takeStepGame(input); // Выводим в консоль шифры
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
