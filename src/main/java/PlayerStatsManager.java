import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.util.Callback;
import javafx.scene.control.TableCell;

import java.util.List;
import java.util.Map;

public class PlayerStatsManager {

    private final TableView<PlayerStat> playerStatsTable;

    public PlayerStatsManager() {
        this.playerStatsTable = new TableView<>();
    }

    /**
     * Creates a table structure for displaying player stats without data.
     *
     * @param group                 The group where the table will be added.
     * @param playerLabels          List of player labels (one for each player).
     * @param rowHeight             The height of each row.
     * @param diceColumnWidth       The width of the player label (icon) column.
     * @param headerOffset          The offset used to position the table (Y position calculation).
     * @param leftBorder            The X position (left border).
     * @param yPosition             The Y position (top of the table).
     * @param tableWidth            The total width of the table.
     */
    public void createPlayerStatsTable(
        Group group, 
        Map<String, Label> playerLabels, 
        int rowHeight, 
        int diceColumnWidth, 
        int headerOffset, 
        int leftBorder, 
        int yPosition, 
        int tableWidth
    ) {
        // Calculate the number of players from the size of the list
        int numPlayers = playerLabels.size();

        // Set the calculated table height based on number of players and row height
        int tableHeight = rowHeight * numPlayers + headerOffset;

        // Set the X and Y position of the table
        playerStatsTable.setPrefSize(tableWidth, tableHeight);
        playerStatsTable.setLayoutX(leftBorder);
        playerStatsTable.setLayoutY(yPosition);

        // Create and set up columns
        setupTableColumns(diceColumnWidth, tableWidth, playerLabels);

        // Set row height
        playerStatsTable.setFixedCellSize(rowHeight);

        // Disable scroll bars
        disableScrollBars(playerStatsTable);

        // Add the empty table to the pane
        group.getChildren().add(playerStatsTable);
    }

    /**
     * Sets up the columns for the player stats table.
     *
     * @param diceColumnWidth The width of the dice (icon) column.
     * @param tableWidth      The total width of the table to adjust other column widths.
     * @param playerLabels    List of player labels to be set in the player column.
     */
    @SuppressWarnings("unchecked")
    private void setupTableColumns(int diceColumnWidth, int tableWidth, Map<String, Label> playerLabels) {
        // Create the "Player" label column (icon column with custom width)
        TableColumn<PlayerStat, Label> labelColumn = new TableColumn<>("Player");
        labelColumn.setCellFactory(new Callback<TableColumn<PlayerStat, Label>, TableCell<PlayerStat, Label>>() {
            @Override
            public TableCell<PlayerStat, Label> call(TableColumn<PlayerStat, Label> param) {
                TableCell<PlayerStat, Label> cell = new TableCell<PlayerStat, Label>() {
                    @Override
                    protected void updateItem(Label item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getIndex() >= playerLabels.size()) {
                            setGraphic(null);
                        } else {
                            String playerName = (String) playerLabels.keySet().toArray()[getIndex()];
                            Label originalLabel = playerLabels.get(playerName);
                            // Create a new label and copy the icon (graphic) from the original label
                            Label newLabel = new Label();
                            if (originalLabel.getGraphic() instanceof ImageView) {
                                ImageView originalImageView = (ImageView) originalLabel.getGraphic();
                                ImageView newImageView = new ImageView(originalImageView.getImage());
                                newLabel.setGraphic(newImageView);
                            } else {
                                newLabel.setGraphic(originalLabel.getGraphic());
                            }
                            setGraphic(newLabel);
                        }
                    }
                };
                cell.setAlignment(Pos.CENTER); // Center the content
                return cell;
            }
        });
        labelColumn.setPrefWidth(diceColumnWidth); // Use the provided dice column width

        // Adjust the remaining table width for the stat columns
        int remainingWidth = tableWidth - diceColumnWidth;

        // Create the "Dollars" column and center the content
        TableColumn<PlayerStat, Integer> dollarsColumn = new TableColumn<>("Dollars");
        dollarsColumn.setCellValueFactory(new PropertyValueFactory<>("dollars"));
        dollarsColumn.setCellFactory(column -> new TableCell<PlayerStat, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
                setAlignment(Pos.CENTER); // Center the content
            }
        });
        dollarsColumn.setPrefWidth(remainingWidth * 0.33); // 33% of remaining width

        // Create the "Credits" column and center the content
        TableColumn<PlayerStat, Integer> creditsColumn = new TableColumn<>("Credits");
        creditsColumn.setCellValueFactory(new PropertyValueFactory<>("credits"));
        creditsColumn.setCellFactory(column -> new TableCell<PlayerStat, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
                setAlignment(Pos.CENTER); // Center the content
            }
        });
        creditsColumn.setPrefWidth(remainingWidth * 0.33); // 33% of remaining width

        // Create the "Tokens" column and center the content
        TableColumn<PlayerStat, Integer> tokensColumn = new TableColumn<>("Tokens");
        tokensColumn.setCellValueFactory(new PropertyValueFactory<>("tokens"));
        tokensColumn.setCellFactory(column -> new TableCell<PlayerStat, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
                setAlignment(Pos.CENTER); // Center the content
            }
        });
        tokensColumn.setPrefWidth(remainingWidth * 0.33); // 33% of remaining width

        // Add all columns to the table
        playerStatsTable.getColumns().addAll(labelColumn, dollarsColumn, creditsColumn, tokensColumn);

        // Add empty PlayerStat objects to the table to match the number of player labels
        for (int i = 0; i < playerLabels.size(); i++) {
            if (i == 0) {
                playerStatsTable.getItems().add(new PlayerStat(i+1,0,0,0, true));
            } else {
                playerStatsTable.getItems().add(new PlayerStat(i+1,0,0,0, false));
            }
        }
    }

    /**
     * Disables the scroll bars of the given TableView.
     *
     * @param tableView The TableView whose scroll bars should be disabled.
     */
    private void disableScrollBars(TableView<?> tableView) {
        tableView.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin != null) {
                // Disable horizontal scroll bar
                ScrollBar hBar = (ScrollBar) tableView.lookup(".scroll-bar:horizontal");
                if (hBar != null) {
                    hBar.setManaged(false);
                    hBar.setVisible(false);
                }

                // Disable vertical scroll bar
                ScrollBar vBar = (ScrollBar) tableView.lookup(".scroll-bar:vertical");
                if (vBar != null) {
                    vBar.setManaged(false);
                    vBar.setVisible(false);
                }
            }
        });
    }
    
    /**
     * Updates the stats for a single player in the table.
     *
     * @param playerID The ID of the player to update.
     * @param dollars New value for dollars.
     * @param credits New value for credits.
     * @param tokens New value for tokens.
     */
    public void updatePlayerStat(int playerID, int dollars, int credits, int tokens) {
        // Iterate over the table's items (PlayerStat instances)
        for (PlayerStat playerStat : playerStatsTable.getItems()) {
            // Find the player by their ID
            if (playerStat.getPlayerID().equals(playerID)) {
                // Update the player's stats
                playerStat.setDollars(dollars);
                playerStat.setCredits(credits);
                playerStat.setTokens(tokens);

                // Refresh the table to reflect the updated row
                playerStatsTable.refresh();
                break;
            }
        }
    }

    /**
     * Adds player stats data to the table.
     *
     * @param playerStats The list of player statistics to be added to the table.
     */
    public void addPlayerData(List<PlayerStat> playerStats) {
        playerStatsTable.getItems().addAll(playerStats);
        playerStatsTable.refresh();
    }

    public TableView<PlayerStat> getPlayerStatsTable() {
        return playerStatsTable;
    }

    /**
     * Highlights the row corresponding to the given row index.
     *
     * @param index The index of the row to be highlighted (0-based index).
     */
    @SuppressWarnings("unchecked")
    public void highlightRow(int index) {
        for (Node node : playerStatsTable.lookupAll(".table-row-cell")) {
            if (node instanceof TableRow) {
                TableRow<PlayerStat> row = (TableRow<PlayerStat>) node;
                if (row.getIndex() == (index - 1)) {
                    row.setStyle("-fx-background-color: #AAAAAA;"); // gray
                } else {
                    row.setStyle(""); // reset to default style
                }
            }
        }
    }

}