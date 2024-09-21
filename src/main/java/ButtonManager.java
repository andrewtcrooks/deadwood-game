import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the buttons for the game.
 */
public class ButtonManager {
    // List to keep track of all buttons
    private final List<Button> buttons = new ArrayList<>();
    
    // List to store all shot ImageViews
    private final List<ImageView> shotImageViews = new ArrayList<>();
    
    // Callback for button clicks
    private BiConsumer<String, Object> onButtonClick;
    
    // Image for the shot counter
    private final Image shotImage;

    /**
     * Creates a new button manager and loads the shot image.
     */
    public ButtonManager() {
        // Load the shot.png image from the resources folder
        Image tempImage = null;
        try {
            tempImage = new Image(getClass().getResourceAsStream("shot.png"));
        } catch (Exception e) {
            System.out.println("Error loading shot.png: " + e.getMessage());
        }
        shotImage = tempImage;
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
    @SuppressWarnings("unchecked")
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
            button.setUserData((Map<String, Object>) data);
        }
        
        // Set the tooltip text
        button.setTooltip(new Tooltip(tooltipText)); 

        // Set the size and position of the button
        button.setLayoutX(area.getX());
        button.setLayoutY(area.getY());
        button.setPrefWidth(area.getW());
        button.setPrefHeight(area.getH());

        // Set button appearance to be mostly transparent with a visible border
        button.setStyle(
            "-fx-background-color: rgba(255, 192, 203, 0.5); " +  // Pink with transparency
            "-fx-border-color: pink; " +                         // Pink border
            "-fx-border-width: 3px;" +
            "-fx-text-fill: transparent;" // Make the text invisible
        );

        // Add action to button
        button.setOnMouseClicked(event -> {
            // If the command is "ACT", place a shot image at the button's location
            if (command.equalsIgnoreCase("ACT")) {
                placeShotImage(group, button);
            }
            
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
    public void removeButtons(Group group) {
        for (Button button : buttons) {
            group.getChildren().remove(button);
        }
        buttons.clear();
    }

        /**
     * Places a shot image at the location of the given button.
     * 
     * @param group  The group where the image will be added.
     * @param button The button whose location determines where the image is placed.
     */
    private void placeShotImage(Group group, Button button) {
        if (shotImage == null) {
            System.out.println("Shot image not loaded.");
            return;
        }
        
        // Create an ImageView for the shot
        ImageView shotImageView = new ImageView(shotImage);
        shotImageView.setFitWidth(30);   // Adjust size as needed
        shotImageView.setFitHeight(30);
        shotImageView.setPreserveRatio(true);
        shotImageView.setSmooth(true);
        shotImageView.setCache(true);

        // Calculate the position: place the shot image near the button
        double shotX = button.getLayoutX() + (button.getPrefWidth() / 2) - (shotImageView.getFitWidth() / 2);
        double shotY = button.getLayoutY() + (button.getPrefHeight() / 2) - (shotImageView.getFitHeight() / 2);

        // Set the position of the shot image
        shotImageView.setLayoutX(shotX);
        shotImageView.setLayoutY(shotY);

        // Add the shot image to the group
        group.getChildren().add(shotImageView);

        // Store the ImageView in the list
        shotImageViews.add(shotImageView);
    }
    
    /**
     * Clears all shot counters from the board.
     */
    public void clearShotCounters(Group group) {
        for (ImageView shotImageView : shotImageViews) {
            group.getChildren().remove(shotImageView);
        }
        shotImageViews.clear();
        System.out.println("All shot counters have been cleared.");
    }
}