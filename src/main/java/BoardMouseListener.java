import java.awt.event.*;
import javax.swing.*;

/**
 * Mouse listener for the board.
 */
class BoardMouseListener implements MouseListener{
    private ButtonManager buttonManager;
    private GameActionListener gameActionListener;

    /**
     * Constructor for the board mouse listener.
     * 
     * @param buttonManager the button manager
     */
    public BoardMouseListener(ButtonManager buttonManager) {
        this.buttonManager = buttonManager;
    }

    /**
     * Handles the mouse click event.
     * 
     * @param e the mouse event
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() instanceof JButton) {
            // Get the button that was clicked
            JButton button = (JButton) e.getSource();
            // Get the action for the button
            Runnable action = this.buttonManager.getAction(button);
            if (action != null) {
                // Run the action
                action.run();
            }
        }
    }

    /**
     * Handles the mouse press event.
     * 
     * @param e the mouse event
     */
    @Override
    public void mousePressed(MouseEvent e) {
    }

    /**
     * Handles the mouse release event.
     * 
     * @param e the mouse event
     */
    @Override
    public void mouseReleased(MouseEvent e) {
    }

    /**
     * Handles the mouse enter event.
     * 
     * @param e the mouse event
     */
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Handles the mouse exit event.
     * 
     * @param e the mouse event
     */
    @Override
    public void mouseExited(MouseEvent e) {
    }

}
