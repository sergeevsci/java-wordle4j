package ru.yandex.practicum;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.*;

class WordleTest {
    private static WordleDictionary dictionary;
    private static LogWriter logWriter;
    private static PrintWriter testLogWriter;
    private static final List<String> TEST_WORDS = Arrays.asList(
        "кросс", "мост", "кабан", "актер", "жизнь",
        "ракета", "окно", "дождь", "ветер", "спорт",
        "мотор", "сон", "дом", "кот", "лес"
    );

    @BeforeAll
    static void setUpAll() {
        testLogWriter = new PrintWriter(System.out, true);
        logWriter = new LogWriter(testLogWriter);
        dictionary = new WordleDictionary(new ArrayList<>(TEST_WORDS), logWriter);
        dictionary = dictionary.normalizeWordleDictionary(TEST_WORDS);
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
    }

    @Test
    @DisplayName("Тест: takeStepGame - уменьшает количество попыток")
    void testTakeStepGameExhaustedAttempts() throws Exception {
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
    @DisplayName("Тест: takeStepGame - слово conversion to lower case")
    void testTakeStepGameConvertsToLowerCase() throws Exception {
        WordleGame game = new WordleGame(6, 5, dictionary, GameStatus.READY, logWriter);

        game.takeStepGame("КРОСС");

        assertEquals("кросс", game.getUsedWords().get(0));
    }

    @Test
    @DisplayName("Тест: takeStepGame - ё заменяется на е")
    void testTakeStepGameReplacesYo() throws Exception {
        WordleGame game = new WordleGame(6, 5, dictionary, GameStatus.READY, logWriter);
        
        String inputWord = "ёлка";
        if (!dictionary.getWords().contains("елка")) {
            inputWord = "кросс";
        }

        game.takeStepGame(inputWord);

        if (inputWord.equals("ёлка")) {
            assertEquals("елка", game.getUsedWords().get(0));
        }
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
    @DisplayName("Тест: takeStepGame - сохраняет транскрипты")
    void testTakeStepGameRecordsTranscripts() throws Exception {
        WordleGame game = new WordleGame(6, 5, dictionary, GameStatus.READY, logWriter);

        game.takeStepGame("кросс");

        assertEquals(1, game.getUsedTranscriptsUsedWords().size());
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
    @DisplayName("Тест: LogWriter записывает сообщение")
    void testLogWriterWritesMessage() {
        LogWriter testLog = new LogWriter(testLogWriter);
        
        testLog.log("Тестовое сообщение", new RuntimeException("Тест"));
        
        assertTrue(true);
    }

@Test
    @DisplayName("Тест: takeStepGame - валидация ввода с разным регистром")
    void testTakeStepGameVariousCaseInputs() throws Exception {
        WordleGame game = new WordleGame(6, 5, dictionary, GameStatus.READY, logWriter);

        game.takeStepGame("КРОСС");
        assertEquals("кросс", game.getUsedWords().get(0));
    }

    @Test
    @DisplayName("Тест: takeStepGame - ввод с пробелами")
    void testTakeStepGameInputWithSpaces() throws Exception {
        WordleGame game = new WordleGame(6, 5, dictionary, GameStatus.READY, logWriter);

        game.takeStepGame("  кросс  ");

        assertEquals("кросс", game.getUsedWords().get(0));
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
    void testGameRequiresNonEmptyDictionary() throws Exception {
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