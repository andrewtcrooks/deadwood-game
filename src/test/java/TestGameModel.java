import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;


public class TestGameModel {
    private GameController controller;
    // private GameModel model;
    private final InputStream systemIn = System.in;
    private ByteArrayInputStream testIn;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private static String boardXMLFilePath = "resources/board.xml";
    private static String cardsXMLFilePath = "resources/cards.xml";

    @BeforeEach
    public void setUpInput() {
        System.setIn(systemIn);
        System.setOut(new PrintStream(outContent));
        GameCLIView.reset();
        GameModel.reset();
        this.controller = new GameController();
    }

    private void setupWithSimulatedInput(String simulatedUserInput) {
        testIn = new ByteArrayInputStream(simulatedUserInput.getBytes());
        System.setIn(testIn);
    }

    private void testNumPlayersSetCorrectly(int numPlayers) {
        setupWithSimulatedInput(numPlayers + "\n");
        controller.initializeGame(GameModel.getInstance(), GameCLIView.getInstance(), boardXMLFilePath, cardsXMLFilePath);
        assertEquals(numPlayers, GameModel.getInstance().getPlayers().size(), 
            "Number of players should be " + numPlayers);
    }

    private void testNumDaysForPlayers(int numPlayers, int expectedNumDays) {
        setupWithSimulatedInput(numPlayers + "\n");
        controller.initializeGame(GameModel.getInstance(), GameCLIView.getInstance(), boardXMLFilePath, cardsXMLFilePath);
        assertEquals(expectedNumDays, GameModel.getInstance().getNumDays(), "Number of days should be " + expectedNumDays + " for " + numPlayers + " players");
    }

    private void testInitialCredits(int numPlayers, int expectedCredits) {
        setupWithSimulatedInput(numPlayers + "\n");
        controller.initializeGame(GameModel.getInstance(), GameCLIView.getInstance(), boardXMLFilePath, cardsXMLFilePath);
        List<Player> players = GameModel.getInstance().getPlayers();
        assertEquals(expectedCredits, players.get(0).getCredits(), 
            "Player 1 should have " + expectedCredits + " credits");
    }

    private void testInitialRank(int numPlayers, int expectedRank) {
        setupWithSimulatedInput(numPlayers + "\n");
        controller.initializeGame(GameModel.getInstance(), GameCLIView.getInstance(), boardXMLFilePath, cardsXMLFilePath);
        List<Player> players = GameModel.getInstance().getPlayers();
        assertEquals(expectedRank, players.get(0).getRank(), 
            "Player 1 should have rank " + expectedRank);
    }

    @AfterEach
    public void tearDown() {
        // Reset the System.in to its original state
        System.setIn(systemIn);
        System.setOut(originalOut);
        GameModel.reset();
        GameCLIView.reset();
    }

    /**
     * Test that GameModel is a singleton by checking that multiple calls to 
     * getInstance return the same instance.
     */
    @Test
    public void testGameModelSingleton() {
        // Simulate user input for number of players
        setupWithSimulatedInput("4\n"); // Assuming setupWithSimulatedInput correctly simulates user input

        // Call getInstance for the first time after the input has been provided
        GameModel firstInstance = GameModel.getInstance();

        // Call getInstance for the second time to check if it returns the same instance
        GameModel secondInstance = GameModel.getInstance();

        // Assert that both calls to getInstance return the same instance, verifying singleton behavior
        assertSame(firstInstance, secondInstance, "GameModel should return the same instance on multiple calls");
    }

    /**
     * Test that GameModel is initialized with the correct number of players.
     */
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5, 6, 7, 8})
    public void testGameModelPlayersSetCorrectly(int numPlayers) {
        testNumPlayersSetCorrectly(numPlayers);
    }

    // /**
    //  * Test that GameModel is initialized with the correct number of days 
    //  * based on the number of players.
    //  */
    @ParameterizedTest
    @CsvSource({
        "2, 3",
        "3, 3",
        "4, 4",
        "5, 4",
        "6, 4",
        "7, 4",
        "8, 4"
    })
    public void testGameModelNumDaysSetCorrectly(int numPlayers, int expectedNumDays) {
        testNumDaysForPlayers(numPlayers, expectedNumDays);
    }
    
    // /**
    //  * Test that GameModel is initialized with the correct number of credits 
    //  * based on the number of players.
    //  */
    @ParameterizedTest
    @CsvSource({
        "2, 0",
        "3, 0",
        "4, 0",
        "5, 2",
        "6, 4",
        "7, 0",
        "8, 0"
    })
    public void testGameModelPlayerInitialCreditsSetCorrectly(int numPlayers, int expectedNumDays) {
        testInitialCredits(numPlayers, expectedNumDays);
    }


    // /**
    //  * Test that GameModel is initialized with the correct rank based on the 
    //  * number of players.
    //  */
    @ParameterizedTest
    @CsvSource({
        "2, 1",
        "3, 1",
        "4, 1",
        "5, 1",
        "6, 1",
        "7, 2",
        "8, 2"
    })
    public void testGameModelPlayerInitialRankSetCorrectly(int numPlayers, int expectedNumDays) {
        testInitialRank(numPlayers, expectedNumDays);
    }

}
