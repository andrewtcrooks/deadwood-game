import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import java.io.PrintStream;

/**
 * Manages the console for the game.
 */
public class ConsoleManager {

    
    /**
     * Constructor for the console manager.
     */
    public ConsoleManager() {
        super();
    }

    /**
     * Create the console.
     * 
     * @param bPane the layered pane
     * @param X the x-coordinate of the console
     * @param Y the y-coordinate of the console
     * @param WIDTH the width of the console
     * @param HEIGHT the height of the console
     */
    public void createConsole(
        Pane pane, 
        int X, 
        int Y, 
        int WIDTH, 
        int HEIGHT,
        int layer
    ) {
        TextArea console = new TextArea();
        console.setEditable(false);

        // Set console properties
        console.setLayoutX(X);
        console.setLayoutY(Y);
        console.setPrefSize(WIDTH, HEIGHT);
        
        // Enable automatic scrolling to the bottom when new text is added
        console.textProperty().addListener((obs, oldText, newText) -> {
            console.setScrollTop(Double.MAX_VALUE);
        });

        // Add the console directly to the pane (no ScrollPane needed)
        pane.getChildren().add(console);


        // Redirect System.out to the console
        System.setOut(new PrintStream(new ConsoleOutputStream(console)));
    }

}