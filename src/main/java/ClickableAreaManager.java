import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import java.util.HashMap;
import java.util.Map;


public class ClickableAreaManager {
    private ButtonManager buttonManager;
    private Map<Area, Button> areaButtons;

    public ClickableAreaManager(ButtonManager buttonManager) {
        super();
        this.buttonManager = buttonManager;
        this.areaButtons = new HashMap<>();
    }

    public void createClickableAreas(
        Pane pane, 
        String command,
        Object data,
        Area area,
        int layer
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
        pane.getChildren().add(button);

    
        // Add the button to the map
        areaButtons.put(area, button);
    }

    /**
     * Removes all clickable areas from the pane.
     * 
     * @param pane the pane from which buttons will be removed
     */
    public void removeClickableAreas(Pane pane) {
        for (Button button : areaButtons.values()) {
            pane.getChildren().remove(button);
        }
        areaButtons.clear();
    }
}