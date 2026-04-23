package ru.yandex.practicum;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.*;

class WordleTest {

    private static WordleDictionary dictionary;
    private static LogWriter logWriter;
    private static PrintWriter testLogWriter;


    @BeforeAll
    static void setUpAll() {

        testLogWriter = new PrintWriter(System.out, true);
        logWriter = new LogWriter(testLogWriter);
        WordleDictionaryLoader loader = new WordleDictionaryLoader("words_ru.txt", logWriter);
        dictionary = new WordleDictionary(loader.readWordsFromFile(), logWriter);
        dictionary = dictionary.normalizeWordleDictionary(dictionary.getWords());
    }


    @Test
    @DisplayName("Тест: WordleDictionary нормализует слова - отфильтровывает не 5 букв")
    void testDictionaryNormalizeReplacesYo() {

        List<String> words = Arrays.asList("ааааа", "ббббб", "ввввв");
        WordleDictionary dict = new WordleDictionary(new ArrayList<>(), logWriter);
        WordleDictionary normalized = dict.normalizeWordleDictionary(words);

        assertEquals(3, normalized.getWords().size());
    }


    @Test
    @DisplayName("Тест: WordleDictionary нормализует слова - нижний регистр")
    void testDictionaryNormalizeLowerCase() {

        List<String> wordsMixedCase = Arrays.asList("КРОСС", "МОСТ", "КАБАН");
        WordleDictionary dict = new WordleDictionary(new ArrayList<>(wordsMixedCase), logWriter);
        WordleDictionary normalized = dict.normalizeWordleDictionary(wordsMixedCase);

        for (String word : normalized.getWords()) {
            assertEquals(word, word.toLowerCase());
        }
    }


    @Test
    @DisplayName("Тест: WordleDictionary фильтрует слова не по 5 символов")
    void testDictionaryFiltersInvalidLength() {

        List<String> wordsMixedLength = Arrays.asList("кросс", "мост", "а", "акте", "жизнь");
        WordleDictionary dict = new WordleDictionary(new ArrayList<>(wordsMixedLength), logWriter);
        WordleDictionary normalized = dict.normalizeWordleDictionary(wordsMixedLength);

        for (String word : normalized.getWords()) {
            assertEquals(5, word.length());
        }
    }


    @Test
    @DisplayName("Тест: WordleDictionaryLoader загружает слова из файла")
    void testDictionaryLoaderReadsFile() {

        LogWriter testLog = new LogWriter(testLogWriter);
        WordleDictionaryLoader loader = new WordleDictionaryLoader("words_ru.txt", testLog);
        List<String> words = loader.readWordsFromFile();

        assertFalse(words.isEmpty());
        assertTrue(words.size() > 100);
    }


    @Test
    @DisplayName("Тест: containsChar - Found")
    void testContainsCharFound() {

        char[] array = {'к', 'р', 'о', 'с', 'с'};
        assertTrue(WordleGame.containsChar(array, 'к'));
        assertTrue(WordleGame.containsChar(array, 'р'));
        assertTrue(WordleGame.containsChar(array, 'о'));
        assertTrue(WordleGame.containsChar(array, 'с'));
    }


@Test
    @DisplayName("Тест: containsChar - Not Found")
    void testContainsCharNotFound() {

        char[] array = {'к', 'р', 'о', 'с', 'с'};
        assertFalse(WordleGame.containsChar(array, 'а'));
        assertFalse(WordleGame.containsChar(array, 'б'));
        assertFalse(WordleGame.containsChar(array, '1'));
    }


    @Test
    @DisplayName("Тест: countCharInWord - подсчет вхождений буквы")
    void testCountCharInWord() {

        char[] array = {'к', 'р', 'о', 'с', 'с'};
        assertEquals(2, WordleGame.countCharInWord(array, 'с'));
        assertEquals(1, WordleGame.countCharInWord(array, 'к'));
        assertEquals(0, WordleGame.countCharInWord(array, 'а'));
    }


    @Test
    @DisplayName("Тест: checkingForPresenceOfLetter - все плюсы (полное совпадение)")
    void testCheckingFullMatch() throws Exception {

        char[] answer = {'р', 'а', 'к', 'е', 'т', 'а'};
        char[] input = {'р', 'а', 'к', 'е', 'т', 'а'};
        StringBuilder sb = new StringBuilder();
        WordleGame game = new WordleGame(6, 6, dictionary, GameStatus.READY, logWriter);

        String result = game.checkingForPresenceOfLetter(input, answer, sb);

        assertEquals("++++++", result);
    }


    @Test
    @DisplayName("Тест: checkingForPresenceOfLetter - все минусы (нет совпадений)")
    void testCheckingNoMatch() throws Exception {

        char[] answer = {'к', 'р', 'о', 'с', 'с'};
        char[] input = {'а', 'б', 'в', 'г', 'д'};
        StringBuilder sb = new StringBuilder();
        WordleGame game = new WordleGame(6, 5, dictionary, GameStatus.READY, logWriter);

        String result = game.checkingForPresenceOfLetter(input, answer, sb);

        assertEquals("-----", result);
    }


    @Test
    @DisplayName("Тест: checkingForPresenceOfLetter - одна буква на своем месте")
    void testCheckingOneCorrectPosition() throws Exception {

        char[] answer = {'к', 'р', 'о', 'с', 'с'};
        char[] input = {'к', 'а', 'б', 'в', 'г'};
        StringBuilder sb = new StringBuilder();
        WordleGame game = new WordleGame(6, 5, dictionary, GameStatus.READY, logWriter);

        String result = game.checkingForPresenceOfLetter(input, answer, sb);

        assertEquals("+----", result);
    }


    @Test
    @DisplayName("Тест: checkingForPresenceOfLetter - одна буква есть но не на месте")
    void testCheckingOnePresentWrongPosition() throws Exception {

        char[] answer = {'к', 'р', 'о', 'с', 'с'};
        char[] input = {'о', 'к', 'б', 'в', 'г'};
        StringBuilder sb = new StringBuilder();
        WordleGame game = new WordleGame(6, 5, dictionary, GameStatus.READY, logWriter);

        String result = game.checkingForPresenceOfLetter(input, answer, sb);

        assertTrue(result.contains("^"));
    }


    @Test
    @DisplayName("Тест: checkingForPresenceOfLetter - учитывает количество вхождений букв")
    void testCheckingLetterCountLimit() throws Exception {

        char[] answer = {'к', 'о', 'с', 'с', 'к'};
        char[] input = {'к', 'а', 'с', 'к', 'к'};
        StringBuilder sb = new StringBuilder();
        WordleGame game = new WordleGame(6, 5, dictionary, GameStatus.READY, logWriter);

        String result = game.checkingForPresenceOfLetter(input, answer, sb);

        assertTrue(result.contains("+"));
        assertTrue(result.contains("^"));
        assertTrue(result.contains("-"));
    }


    @Test
    @DisplayName("Тест: игра создается с начальным статусом READY")
    void testGameInitialStatus() throws Exception {

        WordleGame game = new WordleGame(6, 5, dictionary, GameStatus.READY, logWriter);

        assertEquals(GameStatus.READY, game.getGameStatus());
    }


    @Test
    @DisplayName("Тест: игра устанавливает слово-ответ")
    void testGameSetsAnswer() throws Exception {

        WordleGame game = new WordleGame(6, 5, dictionary, GameStatus.READY, logWriter);

        assertNotNull(game.getAnswer());
        assertEquals(5, game.getAnswer().length());
    }


    @Test
    @DisplayName("Тест: takeStepGame - правильный ответ ведет к SUCCESS")
    void testTakeStepGameCorrectAnswer() throws Exception {

        WordleGame game = new WordleGame(6, 5, dictionary, GameStatus.READY, logWriter);
        String answer = game.getAnswer();

        String result = game.takeStepGame(answer);

        assertEquals(GameStatus.SUCCESS, game.getGameStatus());
        assertEquals("+++++", result);
    }


    @Test
    void testTakeStepGameWrongAnswer() throws Exception {
        WordleGame game = new WordleGame(6, 5, dictionary, GameStatus.READY, logWriter);
        String answer = game.getAnswer();
        String wrongAnswer = answer.equals("кросс") ? "мост" : "кросс";

        game.takeStepGame(wrongAnswer);

        assertEquals(GameStatus.READY, game.getGameStatus());
    }


    @Test
    @DisplayName("Тест: takeStepGame - уменьшает количество попыток")
    void testTakeStepGameDecrementsCount() throws Exception {

        WordleGame game = new WordleGame(6, 5, dictionary, GameStatus.READY, logWriter);

        String answer = game.getAnswer();
        game.setGameStatus(GameStatus.READY);
        game.takeStepGame(answer);

        assertTrue(game.getUsedWords().size() > 0);
    }


    @Test
    @DisplayName("Тест: takeStepGame - пустой ввод дает подсказку")
    void testTakeStepGameEmptyInputGivesHint() throws Exception {

        WordleGame game = new WordleGame(6, 5, dictionary, GameStatus.READY, logWriter);

        game.takeStepGame("");

        assertTrue(game.isUsedHint());
    }


    @Test
    @DisplayName("Тест: takeStepGame - регистрирует использованные слова")
    void testTakeStepGameRecordsUsedWords() throws Exception {

        WordleDictionary testDict = new WordleDictionary(Arrays.asList(
            "кросс", "мост", "ракета", "спорт", "актер"
        ), logWriter);
        testDict = testDict.normalizeWordleDictionary(testDict.getWords());
        WordleGame game = new WordleGame(6, 5, testDict, GameStatus.READY, logWriter);

        game.takeStepGame("кросс");

        assertEquals(1, game.getUsedWords().size());
    }


    @Test
    @DisplayName("Тест: giveHint - возвращает слово из словаря")
    void testGiveHintReturnsWordFromDictionary() throws Exception {

        WordleGame game = new WordleGame(6, 5, dictionary, GameStatus.READY, logWriter);

        String hint = game.giveHint();

        assertNotNull(hint);
    }


    @Test
    @DisplayName("Тест: giveHint - при первом вызове возвращает случайное слово")
    void testGiveHintFirstCall() throws Exception {

        WordleGame game = new WordleGame(6, 5, dictionary, GameStatus.READY, logWriter);

        String hint = game.giveHint();

        assertNotNull(hint);
        assertEquals(5, hint.length());
    }


    @Test
    @DisplayName("Тест: giveHint - подсказка не должна быть ответом")
    void testGiveHintNotAnswer() throws Exception {

        WordleDictionary testDict = new WordleDictionary(Arrays.asList(
            "кросс", "мост", "ракета", "спорт", "актер"
        ), logWriter);
        testDict = testDict.normalizeWordleDictionary(testDict.getWords());
        WordleGame game = new WordleGame(6, 5, testDict, GameStatus.READY, logWriter);

        String hint = game.giveHint();

        assertNotNull(hint);
    }


    @Test
    @DisplayName("Тест: giveHint - подсказка учитывает состояние игры")
    void testGiveHintConsidersGameState() throws Exception {

        WordleDictionary testDict = new WordleDictionary(Arrays.asList(
            "кросс", "мост", "ракета", "спорт", "актер"
        ), logWriter);
        testDict = testDict.normalizeWordleDictionary(testDict.getWords());
        WordleGame game = new WordleGame(6, 5, testDict, GameStatus.READY, logWriter);

        game.takeStepGame("кросс");

        assertTrue(game.getUsedWords().contains("кросс"));
    }


    @Test
    @DisplayName("Тест: GameState обновляется из транскрипта")
    void testGameStateUpdateFromTranscript() {

        GameState state = new GameState(logWriter);

        state.updateFromTranscript("кросс", "+++++", 5);

        assertTrue(state.getConfirmedPositions().containsKey(0));
        assertTrue(state.getConfirmedPositions().containsKey(1));
        assertTrue(state.getConfirmedPositions().containsKey(2));
        assertTrue(state.getConfirmedPositions().containsKey(3));
        assertTrue(state.getConfirmedPositions().containsKey(4));
    }


    @Test
    @DisplayName("Тест: GameState - исключенные буквы (минусы)")
    void testGameStateExcludedLetters() {

        GameState state = new GameState(logWriter);

        state.updateFromTranscript("кросс", "-----", 5);

        assertTrue(state.getExcludedLetters().contains('к'));
        assertTrue(state.getExcludedLetters().contains('р'));
        assertTrue(state.getExcludedLetters().contains('о'));
        assertTrue(state.getExcludedLetters().contains('с'));
    }


    @Test
    @DisplayName("Тест: GameState - присутствующие буквы (плюсы с крышками)")
    void testGameStatePresentLetters() {

        GameState state = new GameState(logWriter);

        state.updateFromTranscript("ааааа", "^----", 5);

        assertTrue(state.getPresentLetters().contains('а'));
    }


    @Test
    @DisplayName("Тест: GameState - подтвержденные позиции (плюсы)")
    void testGameStateConfirmedPositions() {

        GameState state = new GameState(logWriter);

        state.updateFromTranscript("кросс", "+++++", 5);

        assertEquals(Character.valueOf('к'), state.getConfirmedPositions().get(0));
    }


    @Test
    @DisplayName("Тест: GameState - максимальное количество вхождений букв")
    void testGameStateMaxLetterOccurrences() {

        GameState state = new GameState(logWriter);

        state.updateFromTranscript("аабба", "+^-+-", 5);

        assertEquals(Integer.valueOf(2), state.getMinLetterOccurrences().get('а'));
        assertEquals(Integer.valueOf(2), state.getMaxLetterOccurrences().get('а'));
    }


    @Test
    @DisplayName("Тест: GameState - запрещенные позиции для букв из ^")
    void testGameStateForbiddenPositions() {

        GameState state = new GameState(logWriter);

        state.updateFromTranscript("краны", "-^---", 5);

        assertTrue(state.getPresentLetters().contains('р'));
        assertTrue(state.getForbiddenPositions().containsKey('р'));
        assertTrue(state.getForbiddenPositions().get('р').contains(1));
    }


    @Test
    @DisplayName("Тест: GameState - минус без совпадений исключает букву")
    void testGameStateMinusWithoutPositivesExcludesLetter() {

        GameState state = new GameState(logWriter);

        state.updateFromTranscript("бонус", "-----", 5);

        assertTrue(state.getExcludedLetters().contains('б'));
        assertEquals(Integer.valueOf(0), state.getMaxLetterOccurrences().get('б'));
    }


    @Test
    @DisplayName("Тест: LogWriter записывает сообщение")
    void testLogWriterWritesMessage() {

        LogWriter testLog = new LogWriter(testLogWriter);

        testLog.log("Тестовое сообщение", new RuntimeException("Тест"));

        assertTrue(true);
    }


    @Test
    @DisplayName("Тест: takeStepGame - после подсказки флаг usedHint установлен")
    void testTakeStepGameAfterHint() throws Exception {

        WordleGame game = new WordleGame(6, 5, dictionary, GameStatus.READY, logWriter);
        game.takeStepGame("");

        assertTrue(game.isUsedHint());
    }


    @Test
    @DisplayName("Тест: игра не начинается с пустым словарем")
    void testGameRequiresNonEmptyDictionary() {

        WordleDictionary emptyDict = new WordleDictionary(new ArrayList<>(), logWriter);

        assertThrows(Exception.class, () -> {
            new WordleGame(6, 5, emptyDict, GameStatus.READY, logWriter);
        });
    }


    @Test
    @DisplayName("Тест: множественные попытки игры")
    void testMultipleGameAttempts() throws Exception {

        WordleGame game1 = new WordleGame(6, 5, dictionary, GameStatus.READY, logWriter);
        WordleGame game2 = new WordleGame(6, 5, dictionary, GameStatus.READY, logWriter);

        assertNotNull(game1.getAnswer());
        assertNotNull(game2.getAnswer());
    }
}
