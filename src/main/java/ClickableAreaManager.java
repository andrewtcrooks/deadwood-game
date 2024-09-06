import java.awt.Color;
import java.awt.Component;
import java.util.*;
import javax.swing.*;

public class ClickableAreaManager {
    private ButtonManager buttonManager;
    private Map<Area, JButton> areaButtons;

    public ClickableAreaManager(ButtonManager buttonManager) {
        this.buttonManager = buttonManager;
        this.areaButtons = new HashMap<>();
    }

    public void createClickableAreas(
        JLayeredPane bPane, 
        String command,
        Object data,
        Area area,
        int layer
    ) {
        // Create a new clickable area button
        // ClickableAreaButton button = new ClickableAreaButton(command, data);
        JButton button = new JButton();
        button.putClientProperty("command", command);
        button.putClientProperty("data", data);
        
        
        
        // Set the bounds of the button
        int x = area.getX();
        int y = area.getY();
        int w = area.getW();
        int h = area.getH();
        button.setBounds(x, y, w, h);
        // Make the button transparent
        button.setOpaque(true);
        // set invisible
        // TODO: uncomment this when ready to hide buttons
        // button.setVisible(false);
        // Make the button not fill the content area
        button.setContentAreaFilled(false);
        // Make the button paint the border
        button.setBorderPainted(true);
        // Pink with transparency
        button.setBackground(new Color(255, 192, 203, 128));
        // Pink border
        button.setBorder(BorderFactory.createLineBorder(new Color(255, 192, 203), 4)); 
        button.addMouseListener(new BoardMouseListener(buttonManager));
        // Add the button to the layered pane on the EDT
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // Handle the InterruptedException
            e.printStackTrace();
        }
        bPane.add(button, new Integer(layer));
    
        // Add the button to the map
        areaButtons.put(area, button);
    }

    public void removeClickableAreas(JLayeredPane bPane) {
        for (JButton button : areaButtons.values()) {
            bPane.remove(button);
        }
        areaButtons.clear();
        // bPane.revalidate();
        // bPane.repaint();
    }
}