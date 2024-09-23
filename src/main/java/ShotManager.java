import java.util.ArrayList;
import java.util.List;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Manages the shot counters for the game.
 */
public class ShotManager {
    // List to store all shot ImageViews
    private final List<ImageView> shotImageViews = new ArrayList<>();

    // Image for the shot counter
    private final Image shotImage;

    // X offset for the board
    private final Integer BOARD_OFFSET_X;

    /**
     * Initializes a new ShotManager object.
     * 
     * @param BOARD_OFFSET_X
     */
    public ShotManager(int BOARD_OFFSET_X) {
        this.BOARD_OFFSET_X = BOARD_OFFSET_X;
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
     * Places a shot image at the location of the given area.
     * 
     * @param group The group to which the shot image is added.
     * @param area  The area where the image is placed.
     */
    @SuppressWarnings("unused")
    public void placeShotImage(Group group, Area area) {
        if (shotImage == null) {
            System.out.println("Shot image not loaded.");
            return;
        }
        
        // Create an ImageView for the shot
        ImageView shotImageView = new ImageView(shotImage);

        // Place the shot image at the area's location
        int shotX = area.getX() + BOARD_OFFSET_X;
        int shotY = area.getY();

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
        // TODO: remove print line below
        System.out.println("All shot counters have been cleared.");
    }

}
