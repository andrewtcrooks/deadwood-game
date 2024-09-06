import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Manages the buttons for the game.
 */
public class ButtonManager {
    private GameActionListener gameActionListener;


    /**
     * Creates a new button manager.
     * 
     * @param gameActionListener the game action listener
     */
    public ButtonManager(GameActionListener gameActionListener) {
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
     * @return the button
     */
    public JButton createButton(
        String text, 
        int xPos, 
        int yPos, 
        int WIDTH, 
        int HEIGHT
    ) {
        JButton button = new JButton(text);
        button.setBackground(Color.white);
        button.setBounds(xPos, yPos, WIDTH, HEIGHT);
        button.addMouseListener(
            new BoardMouseListener(this)
        );
        return button;
    }

    /**
     * Gets the action for the button.
     * 
     * @param button the button
     * @return the action for the button
     */
    public Runnable getAction(JButton button) {
        String command = (String) button.getClientProperty("command");
        Object data = button.getClientProperty("data");
        if ("MOVE".equals(command)) {
            return () -> this.gameActionListener.onMove((String) data);
        } else if ("WORK".equals(command)) {
            return () -> this.gameActionListener.onWork();
        } else if ("END".equals(command)) {
            return () -> this.gameActionListener.onEnd();
        } else {
            return () -> this.gameActionListener.onSelectNeighbor((String) data);
        }
    }
    
}