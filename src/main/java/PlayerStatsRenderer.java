import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * A custom renderer for the Player Stats JTable.
 */
public class PlayerStatsRenderer extends DefaultTableCellRenderer {
    private int currentPlayerID;
    
    public PlayerStatsRenderer() {
        // Set the alignment of all cells in the Player Stats Table to center
        setHorizontalAlignment(CENTER);
        // Set the current player ID to 1
        this.currentPlayerID = 1;
    }

    /**
     *   Returns the component used for drawing the cell.
     *   Properly renders JLabels in the table.
     */
    @Override
    public Component getTableCellRendererComponent(
        JTable table, 
        Object value, 
        boolean isSelected, 
        boolean hasFocus, 
        int row, 
        int column
    ) {
        if (value instanceof JLabel) {
            // If the value is a JLabel return it
            return (JLabel) value;
        }
        // Otherwise, return the default renderer
        Component component = super.getTableCellRendererComponent(
            table, 
            value, 
            isSelected, 
            hasFocus, 
            row, 
            column
        );
        // Get the player ID from the table model
        PlayerStatsManager model = (PlayerStatsManager) table.getModel();
        // the player ID is in the second column , which has index of 1
        int playerID = (int) model.getValueAt(row, 1); 
        // Highlight the row if it corresponds to the current player
        if (playerID == currentPlayerID) {
            // Change highlight color to Gray
            component.setBackground(Color.GRAY);
            // Make the font bold
            component.setFont(component.getFont().deriveFont(Font.BOLD)); 
        } else {
            component.setBackground(table.getBackground());
            component.setFont(table.getFont());
        }
        // return the component
        return component;
    }

    /**
     * Sets the current player ID for highlighting.
     *
     * @param currentPlayerID the current player ID
     */
    public void setCurrentPlayerID(int currentPlayerID) {
        this.currentPlayerID = currentPlayerID;
    }

}