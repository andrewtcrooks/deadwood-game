import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

    private final TableView<Player> playerStatsTable;
    private final ObservableList<Player> observablePlayerList;

    public PlayerStatsManager() {
        this.playerStatsTable = new TableView<>();
        this.observablePlayerList = FXCollections.observableArrayList();
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

        // Set column resize policy to constrain columns within the TableView
        playerStatsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Create and set up columns
        setupTableColumns(diceColumnWidth, tableWidth, playerLabels);

        // Set row height
        playerStatsTable.setFixedCellSize(rowHeight);

        // Calculate and set the preferred height to fit all rows
        setTableViewHeight(numPlayers, rowHeight);

        // Bind the observable list to the TableView
        playerStatsTable.setItems(observablePlayerList);

        // // Disable scroll bars
        // disableScrollBars(playerStatsTable);

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
        TableColumn<Player, Label> labelColumn = new TableColumn<>("Player");
        labelColumn.setCellFactory(new Callback<TableColumn<Player, Label>, TableCell<Player, Label>>() {
            @Override
            public TableCell<Player, Label> call(TableColumn<Player, Label> param) {
                TableCell<Player, Label> cell = new TableCell<Player, Label>() {
                    @Override
                    protected void updateItem(Label item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getIndex() >= observablePlayerList.size()) {
                            setGraphic(null);
                        } else {
                            Player player = observablePlayerList.get(getIndex());
                            Label originalLabel = playerLabels.get(String.valueOf(player.getID()));
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
                return cell;
            }
        });
        
        // Set fixed width for the first column
        labelColumn.setPrefWidth(48);
        labelColumn.setMinWidth(48);
        labelColumn.setMaxWidth(48);
        labelColumn.setResizable(false);

        // Create the "Dollars" column and center the content
        TableColumn<Player, Number> dollarsColumn = new TableColumn<>("Dollars");
        dollarsColumn.setCellValueFactory(new PropertyValueFactory<>("dollars"));
        dollarsColumn.setCellFactory(column -> new TableCell<Player, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
                setAlignment(Pos.CENTER); // Center the content
            }
        });

        // Create the "Credits" column and center the content
        TableColumn<Player, Number> creditsColumn = new TableColumn<>("Credits");
        creditsColumn.setCellValueFactory(new PropertyValueFactory<>("credits"));
        creditsColumn.setCellFactory(column -> new TableCell<Player, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
                setAlignment(Pos.CENTER); // Center the content
            }
        });

        // Create the "Tokens" column and center the content
        TableColumn<Player, Number> tokensColumn = new TableColumn<>("Tokens");
        tokensColumn.setCellValueFactory(new PropertyValueFactory<>("rehearsalTokens")); // Updated property name
        tokensColumn.setCellFactory(column -> new TableCell<Player, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
                setAlignment(Pos.CENTER); // Center the content
            }
        });

        // Add all columns to the table
        playerStatsTable.getColumns().addAll(labelColumn, dollarsColumn, creditsColumn, tokensColumn);
    }

    /**
     * Calculates and sets the TableView's preferred height based on the number of rows.
     *
     * @param numPlayers The number of player rows.
     * @param rowHeight  The height of each row.
     */
    private void setTableViewHeight(int numPlayers, int rowHeight) {
        // Estimate header height (default is approximately 25 pixels)
        int headerHeight = 25;

        // Calculate total height: header + (rows * rowHeight)
        int totalHeight = headerHeight + (numPlayers * rowHeight) + 3;

        // Set the preferred height
        playerStatsTable.setPrefHeight(totalHeight);
        
        // Optionally, set min and max heights to prevent resizing
        playerStatsTable.setMinHeight(totalHeight);
        playerStatsTable.setMaxHeight(totalHeight);
    }

    // /**
    //  * Disables the scroll bars of the given TableView.
    //  *
    //  * @param tableView The TableView whose scroll bars should be disabled.
    //  */
    // private void disableScrollBars(TableView<?> tableView) {
    //     tableView.skinProperty().addListener((obs, oldSkin, newSkin) -> {
    //         if (newSkin != null) {
    //             // Disable horizontal scroll bar
    //             ScrollBar hBar = (ScrollBar) tableView.lookup(".scroll-bar:horizontal");
    //             if (hBar != null) {
    //                 hBar.setManaged(false);
    //                 hBar.setVisible(false);
    //             }

    //             // Disable vertical scroll bar
    //             ScrollBar vBar = (ScrollBar) tableView.lookup(".scroll-bar:vertical");
    //             if (vBar != null) {
    //                 vBar.setManaged(false);
    //                 vBar.setVisible(false);
    //             }
    //         }
    //     });
    // }
    
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
        for (Player player : playerStatsTable.getItems()) {
            // Find the player by their ID
            if (player.getID() == playerID) {
                // Update the player's stats
                player.setDollars(dollars);
                player.setCredits(credits);
                player.setRehearsalTokens(tokens);

                break;
            }
        }
    }

    /**
     * Adds player data to the table.
     *
     * @param players The list of players to be added to the table.
     */
    public void addPlayerData(List<Player> players) {
        observablePlayerList.addAll(players);
    }

    public TableView<Player> getPlayerStatsTable() {
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
                TableRow<Player> row = (TableRow<Player>) node;
                if (row.getIndex() == index - 1) {
                    row.setStyle("-fx-background-color: #AAAAAA;"); // medium gray
                } else {
                    row.setStyle(""); // reset to default style
                }
            }
        }
    }

}