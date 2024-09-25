import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.util.Callback;
import javafx.scene.control.TableCell;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class PlayerStatsManager {

    private final TableView<Player> playerStatsTable;
    private final ObservableList<Player> observablePlayerList;
    private final String[] diceColor;

    public PlayerStatsManager(String[] diceColor) {
        this.diceColor = diceColor;
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
        setupTableColumns(diceColumnWidth, tableWidth);

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
    private void setupTableColumns(int diceColumnWidth, int tableWidth) {
        // Create the "Player" label column (icon column with custom width)
        TableColumn<Player, Player> labelColumn = new TableColumn<>("Player");
        labelColumn.setPrefWidth(48);
        labelColumn.setMinWidth(48);
        labelColumn.setMaxWidth(48);
        labelColumn.setResizable(false);

        // Set up the cell factory to listen to rank changes and update the icon
        labelColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        labelColumn.setCellFactory(new Callback<TableColumn<Player, Player>, TableCell<Player, Player>>() {
            @Override
            public TableCell<Player, Player> call(TableColumn<Player, Player> param) {
                return new TableCell<Player, Player>() {
                    private final ImageView imageView = new ImageView();
                    private ChangeListener<Number> rankListener;

                    @Override
                    protected void updateItem(Player player, boolean empty) {
                        super.updateItem(player, empty);
                        if (empty || player == null) {
                            setGraphic(null);
                            // Remove existing listener if any
                            if (rankListener != null) {
                                player.rankProperty()
                                      .removeListener(rankListener);
                                rankListener = null;
                            }
                        } else {
                            // Remove previous listener if any
                            if (rankListener != null) {
                                player.rankProperty()
                                      .removeListener(rankListener);
                            }

                            // Set initial image based on current rank
                            updateImage(player);

                            // Add listener to update image when rank changes
                            rankListener = (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                                updateImage(player);
                            };
                            player.rankProperty().addListener(rankListener);

                            setGraphic(imageView);
                        }
                    }

                    /**
                     * Updates the dice icon based on the player's current rank.
                     */
                    private void updateImage(Player player) {
                        String diceFilename = diceColor[player.getID() - 1] + player.getRank() + ".png";
                        InputStream diceImageStream = getClass().getClassLoader().getResourceAsStream("dice/" + diceFilename);
                        if (diceImageStream != null) {
                            Image diceImage = new Image(diceImageStream);
                            imageView.setImage(diceImage);
                        } else {
                            System.err.println("Error loading dice image: " + diceFilename);
                            imageView.setImage(null); // Optionally set a default image
                        }
                        imageView.setFitWidth(40); // Adjust size as needed
                        imageView.setFitHeight(40);
                    }
                };
            }
        });

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
        tokensColumn.setCellValueFactory(new PropertyValueFactory<>("rehearsalTokens"));
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