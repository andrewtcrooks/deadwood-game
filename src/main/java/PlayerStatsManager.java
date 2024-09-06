import javax.swing.DefaultListSelectionModel;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.util.*;

//stuff 

/**
 * Manages the player stats for the game.
 */
public class PlayerStatsManager extends AbstractTableModel {
    private JTable playerStatsTable;
    private PlayerStatsRenderer cellRenderer;
    // Column names for the player stats table
    private final String[] columnNames = {
        "Player  ", "ID", "Dollars", "Credits", "Tokens"
    };
    // Map of player stats with JLabel as key and List of stats as value, 
    // sorted by player ID
    private Map<JLabel, List<Integer>> playerStats = new TreeMap<>(
        Comparator.comparingInt(label -> 
            Integer.parseInt(label.getText().replaceAll("\\D+", ""))
        )
    );

    
    /**
     * Constructor for the player stats manager.
     */
    public PlayerStatsManager() {
        // Create the player stats table
        this.playerStatsTable = new JTable(this);
        // Create a custom renderer for player stats table
        this.cellRenderer = new PlayerStatsRenderer();
        // Disable row selection
        playerStatsTable.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                // Do nothing to prevent creating a clickable table
            }
        });
    }

    /**
     * Creates the player stats table.
     * 
     * @param bPane the layered pane
     * @param numPlayers the number of players
     * @param BOARD_IMAGE_WIDTH the width of the board image
     * @param ROW_HEIGHT the height of each row
     * @param DICE_COLUMN_WIDTH the width of the dice column
     * @param HEADER_OFFSET the offset of the header
     * @param HEADER_HEIGHT the height of the header
     * @param X the x-coordinate of the player stats table
     * @param Y the y-coordinate of the player stats table
     * @param WIDTH the width of the player stats table
     * @param layer the layer of the player stats table
     */
    public void createPlayerStatsTable(
        JLayeredPane bPane, 
        int numPlayers, 
        int BOARD_IMAGE_WIDTH, 
        int ROW_HEIGHT, 
        int DICE_COLUMN_WIDTH, 
        int HEADER_OFFSET, 
        int HEADER_HEIGHT, 
        int X, 
        int Y, 
        int WIDTH,
        int layer
    ) {
        // Set the row height based on the dice image height
        playerStatsTable.setRowHeight(ROW_HEIGHT);
        // Set custom renderer for the all columns and make the first column 
        // only as wide as the dice labels
        for (int i = 0; i < columnNames.length; i++) {
            // Get the column
            TableColumn column = playerStatsTable.getColumnModel().getColumn(i);
            if (i == 0) {
                // Set the width of the first column
                column.setPreferredWidth(DICE_COLUMN_WIDTH);
                column.setMinWidth(DICE_COLUMN_WIDTH);
                column.setMaxWidth(DICE_COLUMN_WIDTH);
            }
            // Set the custom renderer for the column
            column.setCellRenderer(this.cellRenderer);
        }
        // Set the column headers
        JTableHeader header = playerStatsTable.getTableHeader();
        header.setReorderingAllowed(false);
        // Set the default header renderer and center alignment
        DefaultTableCellRenderer headerRenderer = 
            (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        // Set the custom header value and renderer for each column
        for (int i = 0; 
             i < playerStatsTable.getColumnModel().getColumnCount(); 
             i++
        ) {
            playerStatsTable.getColumnModel()
                            .getColumn(i)
                            .setHeaderValue(columnNames[i]);
        }
        // Set the bounds of the header
        header.setBounds(X, Y, WIDTH, HEADER_HEIGHT);
        // Set the bounds of the player stats table
        playerStatsTable.setBounds(
            X, 
            Y + HEADER_OFFSET, 
            WIDTH, 
            ROW_HEIGHT*numPlayers
        );
        // Add the player stats table to the layered pane
        bPane.add(playerStatsTable, new Integer(2));
        // Add the table header to the layered pane
        bPane.add(header, new Integer(2));
    }

    /**
     * Sets the current player ID for highlighting.
     * 
     * @param currentPlayerID the current player ID
     */
    public void setCurrentPlayerID(int currentPlayerID) {
        // Set the current player ID for the cell renderer
        this.cellRenderer.setCurrentPlayerID(currentPlayerID);
        // Repaint the table to apply the highlight
        this.playerStatsTable.repaint();
    }

    /**
     * Updates the given player's stats row with the new stats.
     * 
     * @param playerDiceLabels the map of player dice labels
     * @param playerID the player ID
     * @param dollars the player's dollars
     * @param credits the player's credits
     * @param rehearsalTokens the player's rehearsal tokens
     */
    public void updateStats(
        Map<String, JLabel> playerDiceLabels, 
        Integer playerID, 
        Integer dollars, 
        Integer credits, 
        Integer rehearsalTokens
    ) {
        // Retrieve the JLabel associated with the playerID
        JLabel playerLabel = playerDiceLabels.get(playerID.toString());
        // Update the playerStats map with the new stats
        playerStats.put(playerLabel, Arrays.asList(
            playerID, 
            dollars, 
            credits, 
            rehearsalTokens
            )
        );
        // Determine the row index of the updated player
        int rowIndex = 
            new ArrayList<>(playerStats.keySet()).indexOf(playerLabel);
        // Notify listeners that the row at rowIndex has been updated
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    /**
     * Returns the value at the specified row and column.
     * 
     * @param rowIndex the row index
     * @param columnIndex the column index
     * @return the value at the specified row and column
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        // Retrieve the JLabels for the players
        List<JLabel> keys = new ArrayList<>(playerStats.keySet());
        // Retrieve the JLabel for the player at the rowIndex
        JLabel key = keys.get(rowIndex);
        // Retrieve the stats for the player associated with the key
        List<Integer> stats = playerStats.get(key);
        if (columnIndex == 0) {
            // Return JLabel for the first column
            return key; 
        }
        // Adjust index for other columns
        return stats.get(columnIndex - 1); 
    }

    /**
     * Returns the number of rows in the table.
     * 
     * @return the number of rows
     */
    @Override
    public int getRowCount() {
        return playerStats.size();
    }

    /**
     * Returns the number of columns in the table.
     * 
     * @return the number of columns
     */
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

}
