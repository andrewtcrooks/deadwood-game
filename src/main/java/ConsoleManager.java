import javafx.scene.control.ScrollPane;
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
        ScrollPane scrollPane = new ScrollPane(console);
        scrollPane.setLayoutX(X);
        scrollPane.setLayoutY(Y);
        scrollPane.setPrefSize(WIDTH, HEIGHT);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        // Scroll to the bottom when new text is added
        console.textProperty().addListener((obs, oldText, newText) -> {
            console.setScrollTop(Double.MAX_VALUE);
        });

        // Add the console to the pane
        pane.getChildren().add(scrollPane);

        // Redirect System.out to the console
        System.setOut(new PrintStream(new ConsoleOutputStream(console)));
    }

}