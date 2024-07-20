import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestDeadwood {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private ByteArrayInputStream testIn;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(System.in);
    }

    @Test
    public void testGameFlowWithMockInput() {
        String input = "4\nwho\nmove\nHotel\nwork\nFaro player\nend\nend\nend\nwho\nrehearse\nend\nend\nend\nrehearse\nend\nend\nend\nrehearse\nend\nend\nend\nrehearse\nend\nend\nend\nact\nend\nend\nend\nact\nend\nend\nend\n";
        testIn = new ByteArrayInputStream(input.getBytes());
        System.setIn(testIn);
    
        // Start the game here. This might involve calling the main method or another method that starts the game loop.
        Deadwood.main(new String[]{});
    
        // Convert the output stream to a string, then split it into lines
        String[] outputLines = outContent.toString().split("\\r?\\n");
    
        // Select the last line from the output
        String lastLine = outputLines[outputLines.length - 1];
    
        // Assertions to verify the last line of the game output contains a specific message indicating the game state
        assertTrue(lastLine.contains("Expected output or game state indication"));
    }
}