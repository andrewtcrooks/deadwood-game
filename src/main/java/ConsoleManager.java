import javafx.scene.control.TextArea;
import javafx.scene.Group;
import java.io.PrintStream;

/**
 * Manages the console for the game.
 */
public class ConsoleManager {


    /**
     * Create the console.
     * 
     * @param group the group to which to add the console
     * @param X the x-coordinate of the console
     * @param Y the y-coordinate of the console
     * @param WIDTH the width of the console
     * @param HEIGHT the height of the console
     */
    public void createConsole(
        Group group, 
        int X, 
        int Y, 
        int WIDTH, 
        int HEIGHT
    ) {
        TextArea console = new TextArea();
        console.setEditable(false);

        // Set console properties
        console.setLayoutX(X);
        console.setLayoutY(Y);
        console.setPrefSize(WIDTH, HEIGHT);

        // Apply CSS to center text horizontally and vertically
        console.setStyle("-fx-text-alignment: center; -fx-alignment: center;");
        
        // Enable automatic scrolling to the bottom when new text is added
        console.textProperty().addListener((obs, oldText, newText) -> {
            console.setScrollTop(Double.MAX_VALUE);
        });

        // Add the console directly to the group
        group.getChildren().add(console);


        // Redirect System.out to the console
        System.setOut(new PrintStream(new ConsoleOutputStream(console)));
    }

}