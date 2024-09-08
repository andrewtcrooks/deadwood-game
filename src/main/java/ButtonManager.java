import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.input.MouseEvent;
import java.util.Map;


// import java.util.Arrays;
import java.util.HashMap;

/**
 * Manages the buttons for the game.
 */
public class ButtonManager {
    private GameActionListener gameActionListener;
    private Map<Button, Runnable> buttonActions = new HashMap<>();


    /**
     * Creates a new button manager.
     * 
     * @param gameActionListener the game action listener
     */
    public ButtonManager(GameActionListener gameActionListener) {
        super();
        this.gameActionListener = gameActionListener;
    }

    /**
     * Creates a button.
     * 
     * @param text the text of the button
     * @param xPos the x-coordinate of the button
     * @param yPos the y-coordinate of the button
     * @param WIDTH the width of the button
     * @param HEIGHT the height of the button
     * @param pane the pane to which the button will be added
     * @return the button
     */
    public Button createButton(
        String text, 
        int xPos, 
        int yPos, 
        int WIDTH, 
        int HEIGHT,
        Pane pane
    ) {
        // Set button properties
        Button button = new Button(text);
        button.setStyle("-fx-background-color: white;");
        button.setLayoutX(xPos);
        button.setLayoutY(yPos);
        button.setMinWidth(WIDTH);
        button.setMinHeight(HEIGHT);
        
        // Add event handler to the button
        button.addEventHandler(
            MouseEvent.MOUSE_CLICKED, 
            new BoardMouseListener(this)
        );
        
        // Add the button to the provided pane
        pane.getChildren().add(button);
        return button;
    }

    /**
     * Gets the action for the button.
     * 
     * @param button the button
     * @return the action for the button
     */
    public Runnable getAction(Button button) {
        String command = (String) button.getUserData(); // Using setUserData/getUserData in JavaFX
        if ("MOVE".equals(command)) {
            return () -> this.gameActionListener.onMove((String) button.getUserData());
        } else if ("WORK".equals(command)) {
            return () -> this.gameActionListener.onWork();
        } else if ("END".equals(command)) {
            return () -> this.gameActionListener.onEnd();
        } else {
            return () -> this.gameActionListener.onSelectNeighbor((String) button.getUserData());
        }
    }

    // TODO: fix this it seems off
    /**
     * Associates a command and data with a button.
     * 
     * @param button the button to associate
     * @param command the command string
     * @param data the data associated with the command
     */
    public void setButtonAction(Button button, String command, Object data) {
        button.setUserData(data); // Store data in the button
        // Add a mapping to the action for the button
        buttonActions.put(button, getAction(button));
    }
    
}