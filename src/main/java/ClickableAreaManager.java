import javafx.scene.control.Button;
import javafx.scene.Group;
import java.util.HashMap;
import java.util.Map;


public class ClickableAreaManager {
    private ButtonManager buttonManager;
    private Map<Area, Button> areaButtons;

    /**
     * Constructor for the ClickableAreaManager.
     * 
     * @param buttonManager the ButtonManager to use
     */
    public ClickableAreaManager(ButtonManager buttonManager) {
        super();
        this.buttonManager = buttonManager;
        this.areaButtons = new HashMap<>();
    }

    /**
     * Creates clickable areas on the pane.
     * 
     * @param group the group to which to add the clickable areas
     * @param command the command to execute when the area is clicked
     * @param data the data associated with the clickable area
     * @param area the area to make clickable
     */
    public void createClickableAreas(
        Group group, 
        String command,
        Object data,
        Area area
    ) {
        // Create a new clickable area button
        Button button = new Button(command);
        button.setUserData(data); // Set the associated data
        
        // Set the size and position of the button
        button.setLayoutX(area.getX());
        button.setLayoutY(area.getY());
        button.setMinWidth(area.getW());
        button.setMinHeight(area.getH());

        // Set button appearance to be mostly transparent with a visible border
        button.setStyle(
            "-fx-background-color: rgba(255, 192, 203, 0.5); " +  // Pink with transparency
            "-fx-border-color: pink; " +                         // Pink border
            "-fx-border-width: 4px;"
        );

        // Add action to button
        button.setOnMouseClicked(event -> {
            Runnable action = buttonManager.getAction(button);
            if (action != null) {
                action.run();
            }
        });

        // Add button to the pane
        group.getChildren().add(button);

    
        // Add the button to the map
        areaButtons.put(area, button);
    }

    /**
     * Removes all clickable areas from the pane.
     * 
     * @param pane the pane from which buttons will be removed
     */
    public void removeClickableAreas(Group group) {
        for (Button button : areaButtons.values()) {
            group.getChildren().remove(button);
        }
        areaButtons.clear();
    }
}