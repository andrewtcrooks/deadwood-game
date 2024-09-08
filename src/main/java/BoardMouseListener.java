import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

/**
 * Mouse listener for the board.
 */
class BoardMouseListener implements EventHandler<MouseEvent>{
    private ButtonManager buttonManager;

    /**
     * Constructor for the board mouse listener.
     * 
     * @param buttonManager the button manager
     */
    public BoardMouseListener(ButtonManager buttonManager) {
        super();
        this.buttonManager = buttonManager;
    }

    /**
     * Handles the mouse click event.
     * 
     * @param e the mouse event
     */
    @Override
    public void handle(MouseEvent e) {
        if (e.getSource() instanceof Button) {
            // Get the button that was clicked
            Button button = (Button) e.getSource();
            // Get the action for the button
            Runnable action = this.buttonManager.getAction(button);
            if (action != null) {
                // Run the action
                action.run();
            }
        }
    }

}
