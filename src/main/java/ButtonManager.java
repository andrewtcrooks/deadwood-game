import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import java.util.function.BiConsumer;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the buttons for the game.
 */
public class ButtonManager {
    // List to keep track of all buttons
    private final List<Button> buttons = new ArrayList<>();
    
    // Callback for button clicks
    private BiConsumer<String, Object> onButtonClick;
    
    /**
     * Creates a new button manager and loads the shot image.
     */
    public ButtonManager() {

    }

    /**
     * Creates a button.
     * 
     * @param group the group
     * @param command the command
     * @param data the data
     * @param area the area
     * @param tooltipText the tooltip text
     *
     * @return the button
     */
    public void createButton(
        Group group, 
        String command,
        Object data,
        Area area,
        String tooltipText
    ) {        
        // Create a new clickable area button with command as label
        Button button = new Button(command);
        
        // Set the associated data type
        if (command.equalsIgnoreCase("MOVE") || command.equalsIgnoreCase("WORK")) {
            button.setUserData((String) data);
        }
        else if (command.equals("UPGRADE")){
            button.setUserData((Upgrade) data); // 'data' is an Upgrade object
        }
        
        // Set the tooltip text
        button.setTooltip(new Tooltip(tooltipText)); 

        // Set the size and position of the button
        button.setLayoutX(area.getX());
        button.setLayoutY(area.getY());
        button.setPrefWidth(area.getW());
        button.setPrefHeight(area.getH());

        // Enforce exact button dimensions by setting min and max sizes
        button.setMinWidth(area.getW());
        button.setMinHeight(area.getH());
        button.setMaxWidth(area.getW());
        button.setMaxHeight(area.getH());

        // Remove padding to prevent automatic size adjustments
        button.setPadding(new Insets(0));

        // Set button appearance to be mostly transparent with a visible border
        button.setStyle(
            "-fx-background-color: rgba(255, 192, 203, 0.5); " +  // Pink with transparency
            "-fx-border-color: pink; " +                         // Pink border
            "-fx-border-width: 3px;" +
            "-fx-text-fill: transparent;" // Make the text invisible
        );

        // Add action to button
        button.setOnMouseClicked(event -> {
            // Notify any additional listeners through the callback
            if (onButtonClick != null) {
                onButtonClick.accept(command, data);
            }

        });

        // Add button to the group
        group.getChildren().add(button);
    
        // Add the button to the list of buttons
        buttons.add(button);
    }
    
    /**
     * Set a callback for when a button is clicked.
     * 
     * @param onButtonClick a BiConsumer that handles command and data when clicked
     */
    public void setOnButtonClick(BiConsumer<String, Object> onButtonClick) {
        this.onButtonClick = onButtonClick;
    }

    /**
     * Removes all clickable areas from the pane.
     * 
     * @param pane the pane from which buttons will be removed
     */
    public void clearAllButtons(Group group) {
        for (Button button : buttons) {
            group.getChildren().remove(button);
        }
        buttons.clear();
    }


}