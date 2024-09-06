import java.util.concurrent.CountDownLatch;
import javax.swing.*;
import java.util.*;
import java.awt.Color;
import java.awt.Container;

public class GameGUIView extends JFrame implements GameView {

    private static final String[] diceColor = {
        "r", "o", "y", "g", "c", "b", "v", "p"
    };

    // JLabels
    JLabel boardLabel;
    // JLabel cardLabel;
    Map<String, JLabel> playerDiceLabels = new HashMap<String, JLabel>();

    // JLayered Pane
    JLayeredPane bPane;

    // Singleton instance
    private static GameGUIView instance;

    // User Input
    private String input;

    // CountDownLatch
    private CountDownLatch latch;

    // Game Action Listener
    private GameActionListener gameActionListener;

    // Managers
    // private MenuManager menuManager;
    private ButtonManager buttonManager;
    private ConsoleManager consoleManager;
    private PlayerStatsManager playerStatsManager;
    private ClickableAreaManager clickableAreaManager;

    private int BOARD_IMAGE_WIDTH = 1200;

    // Constants
    private static final int BOARD_LAYER = 0;
    private static final int SCENE_CARD_LAYER = 1;
    private static final int CONSOLE_LAYER = 1;
    private static final int PSTATS_LAYER = 1;
    private static final int SCENE_CARD_COVER_LAYER = 2;
    private static final int PLAYER_DICE_LAYER = 10;
    
    
    private static final int SIDEBAR_LEFT_BORDER = 15;
    private static final int SIDEBAR_RIGHT_BORDER = 8;
    // private static final int MENU_Y = 10;
    private static final int SIDEBAR_WIDTH = 300;
    private static final int BOARD_OFFSET = SIDEBAR_LEFT_BORDER + 
                                            SIDEBAR_WIDTH + 
                                            SIDEBAR_RIGHT_BORDER;
    // private static final int MENU_HEIGHT = 172;
    // private static final int MENU_HEADER_Y = 5;
    // private static final int MENU_HEADER_HEIGHT = 20;
    
    // private static final int BUTTON_X = 30;
    // private static final int BUTTON_SPACING = 8;
    // private static final int BUTTON_WIDTH = 240;
    // private static final int BUTTON_HEIGHT = 20;

    // private static final int CONSOLE_X = 10;
    private static final int CONSOLE_Y = 16;
    private static final int CONSOLE_WIDTH = 300;
    private static final int CONSOLE_HEIGHT = 464;

    // private static final int PSTATS_X = 10;
    private static final int PSTATS_Y = 497;
    private static final int PSTATS_ROW_HEIGHT = 46;
    private static final int PSTATS_DICE_COLUMN_WIDTH = 46;
    private static final int PSTATS_HEADER_OFFSET = 20;
    private static final int PSTATS_HEADER_HEIGHT = 20;
    private static final int PSTATS_WIDTH = 300;

    private static final int CARD_HEIGHT = 115;
    private static final int CARD_WIDTH = 205;

    private static final int DICE_HEIGHT = 40;
    private static final int DICE_WIDTH = 40;

    private static final int SHOT_HEIGHT = 42;
    private static final int SHOT_WIDTH = 42;

/*******************************
 * Contructor
 *******************************/

    public GameGUIView() {
        // Set the title of the JFrame
        super("Deadwood");

        // Set the exit option for the JFrame
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Prevent resizing window (forces repaint)
        setResizable(false);

        // Set the background color of the content pane to brown
        getContentPane().setBackground(new Color(175, 115, 74, 255));
        getContentPane().setForeground(Color.WHITE);

        // Create the JLayeredPane to hold the display, cards, dice and buttons
        this.bPane = getLayeredPane();

        // Set null layout to manually control positioning
        this.bPane.setLayout(null);



        // JButton testButton = new JButton("Test Button");
        // testButton.setBounds(50, 50, 100, 40);
        // bPane.add(testButton, new Integer(4));  // Add to a higher layer

        // JLabel testDice = new JLabel("Dice");
        // testDice.setBounds(200, 200, 50, 50);
        // bPane.add(testDice, new Integer(3));  // Add to a lower layer

        // JButton clickableButton = new JButton("Clickable Area");
        // clickableButton.setBounds(300, 300, 100, 40);
        // bPane.add(clickableButton, new Integer(4));  // Higher layer

        // bPane.revalidate();
        // bPane.repaint();


        // Create the deadwood board and boardLabel
        createBoard();
        // Create the player stats manager, and console manager
        this.consoleManager = new ConsoleManager();
        this.playerStatsManager = new PlayerStatsManager();
        
        // Create the console 
        this.consoleManager.createConsole(
            this.bPane, 
            this.boardLabel,
            SIDEBAR_LEFT_BORDER,
            CONSOLE_Y,
            CONSOLE_WIDTH,
            CONSOLE_HEIGHT,
            CONSOLE_LAYER
        );
        // Create the file menu
        createFileMenu();
    }


/*******************************
 * Singleton Pattern
 *******************************/

    /**
     * Get the singleton instance of the GameGUIView.
     * @return the singleton instance
     */
    public static GameGUIView getInstance() {
        if (instance == null) {
            instance = new GameGUIView();
        }
        return instance;
    }


/*******************************
 * Initialization Methods
 *******************************/

    /**
     * Create board.
     */
    private void createBoard() {
        // Add board image to GUI
        this.boardLabel = new JLabel();
        ImageIcon icon = 
            new ImageIcon(getClass().getClassLoader().getResource(
                "board.jpg"));
        this.boardLabel.setIcon(icon);
        int indent = BOARD_OFFSET;
        this.boardLabel.setBounds(
            indent,
            0,
            icon.getIconWidth(),
            icon.getIconHeight()
        );
        // Add board to the lowest layer
        bPane.add(this.boardLabel, new Integer(BOARD_LAYER));
        // Set size of GUI
        setSize(
            icon.getIconWidth()+ indent, 
            icon.getIconHeight()+28
        );
        bPane.setOpaque(true);
    }

    /**
     * Create the file menu.
     */
    private void createFileMenu() {
        // Create the menu bar
        JMenuBar menuBar = new JMenuBar();
        // Create the "File" menu
        JMenu fileMenu = new JMenu("File");
        // Create the "Load" menu item
        JMenuItem loadMenuItem = new JMenuItem("Load");
        loadMenuItem.addActionListener(
            e -> showMessage("Load option selected")
        );
        // Create the "Save" menu item
        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.addActionListener(
            e -> showMessage("Save option selected")
        );
        // Add menu items to the "File" menu
        fileMenu.add(loadMenuItem);
        fileMenu.add(saveMenuItem);
        // Add the "File" menu to the menu bar
        menuBar.add(fileMenu);
        // Set the menu bar to the frame
        setJMenuBar(menuBar);
    }


/*******************************
 * GameView Methods
 *******************************/

    /**
     * Get the player's input.
     * 
     * @return The player's input
     */
    @Override
    public String getPlayerInput() {
        latch = new CountDownLatch(1);
        try {
            latch.await(); // Wait for the button click
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get the number of players.
     * 
     * @return The number of players
     */
    public int getNumPlayers() {
        // // Load the custom icon
        // TODO: double check if commented out code is needed
        // ImageIcon icon = 
        //  new ImageIcon(getClass().getResource("deadwood_app_icon.png"));
        // Set the icon to null
        UIManager.put(
            "OptionPane.questionIcon", 
            new ImageIcon(new byte[0])
        );
        // Take input from the user about number of players
        while(true) {
            try {
                // Show the input dialog
                String input = (String) JOptionPane.showInputDialog(
                    this, 
                    "How many players? (between 2 and 8):", 
                    "Deadwood", 
                    JOptionPane.QUESTION_MESSAGE, 
                    null, 
                    null, 
                    null
                );
                // Handle the case where the user enters nothing
                if (input == null) {
                    System.exit(0);
                    continue;
                }
                // Check if the non-null input is an integer
                int numPlayers = Integer.parseInt(input);
                if (numPlayers >= 2 && numPlayers <= 8) {
                    return numPlayers;
                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        "Invalid number of players. Please enter a number " +
                        "between 2 and 8",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } catch (InputMismatchException e) {
                JOptionPane.showMessageDialog(
                    this, 
                    "Invalid input. Please enter an integer.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    /**
     * Set the GameActionListener and finish loading the GUI Menu.
     */
    public void setGameActionListener(GameActionListener listener) {
        this.gameActionListener = listener;
        this.buttonManager = new ButtonManager(this.gameActionListener);
        // Create the clickable areas
        this.clickableAreaManager = new ClickableAreaManager(this.buttonManager);
    }

/************************************************************
 * Observer Pattern
 ************************************************************/

    /**
     * Updates the view.
     * 
     * @param arg The argument to update the view
     */
    @Override
    public void update(String eventType, Object eventData) {
        switch (eventType) {
            case "SHOW_MESSAGE":
                showMessage((String) eventData);
                break;
            case "INIT_DICE_LABELS":
                showMessage("Welcome to Deadwood!");
                initDiceLabels(eventData);
                break;
            case "ADD_CARD":
                addCard(eventData);
                break;
            case "ADD_CARD_BACKS":
                addCardBack(eventData);
                break;
            case "CREATE_PLAYER_STATS_TABLE":
                createPlayerStats(eventData);
                break;
            case "UPDATE_ALL_PLAYER_STATS":
                updateAllPlayerStats(eventData);
                break;
            case "SHOW_BOARD":
                showBoard();
                break;



            case "ADD_CLICKABLE_AREA":
                @SuppressWarnings("unchecked") 
                Map<String,Object> data = (Map<String,Object>) eventData;
                String command1 = data.get("command").toString();
                String command2 = data.get("data").toString();
                Area area = (Area) data.get("area");
                Area newArea = new Area(
                    area.getX() + BOARD_OFFSET, 
                    area.getY(), 
                    area.getH(), 
                    area.getW()
                );
                clickableAreaManager.createClickableAreas(this.bPane, command1, command2, newArea, 4);                
                break;



            case "REMOVE_CLICKABLE_AREAS":
                Area remArea = (Area) eventData;
                removeMouseListenerFromArea(remArea);
                break;
            case "UPDATE_INVISIBLE_BUTTONS":
                updateInvisibleButtons();
                break;
            // case "SHOW_MOVE_MENU":
            //     // overlay Move Menu over Action Menu, show possible moves
            //     showMoveMenu(eventData);
            //     break;
            case "MOVE_TO_LOCATION":
                handlePlayerMove(eventData);
                // hideMoveMenu();
                break;
            // case "CANCEL_MOVE_MENU":
            //     // overlay Location Menu over Action Menu, show possible moves
            //     hideMoveMenu();
            //     break;
            case "LOAD_GAME":
                handleLoadGame(eventData);
                break;
            case "SCORE_UPDATE":
                handleScoreUpdate(eventData);
                break;
            case "GAME_EVENT":
                handleGameEvent(eventData);
                break;
            default:
                System.out.println("Unknown event type: " + eventType);
        }
    }


/************************************************************
 * Observer Pattern Methods
 ************************************************************/

    /**
     * Display a message.
     * 
     * @param message The message to display
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Create player dice labels.
     * 
     * @param eventData number of players (int)
     */
    @SuppressWarnings("unchecked")
    private void initDiceLabels(Object eventData) {
        SwingUtilities.invokeLater(() -> {
        
            // Cast eventData to Map<Integer, Map<String, Integer>>
            Map<Integer, Map<String, Integer>> playerData = 
                (Map<Integer, Map<String, Integer>>) eventData;
            // numPlayers is the size of the playerData map
            int numPlayers = playerData.size();
            // Determine initial dice rank based on number of players
            int rank = numPlayers < 7 ? 1 : 2; // 1 if numPlayers under 7, else 2
            // Create player dice labels and store them in playerDiceLabels
            for (Map.Entry<Integer, Map<String, Integer>> entry : 
                playerData.entrySet()) {
                Integer playerID = entry.getKey();
                Map<String, Integer> playerInfo = entry.getValue();
                int x = playerInfo.get("locationX");
                int y = playerInfo.get("locationY");
                // Add offset to x to compensate for left sidebar
                x += BOARD_OFFSET;
                // Create a new JLabel for the player dice
                JLabel playerDiceLabel = new JLabel("Player" + playerID);
                // Add the player dice label to the playerDiceLabels map
                this.playerDiceLabels.put(playerID.toString(), playerDiceLabel);
                // Create filename reference for dice image
                String diceFilename = diceColor[playerID-1] + rank;
                // Set the icon for the dice label, 0.7f for 70% transparency
                setDiceLabelIcon(playerDiceLabel, diceFilename, 0.70f);
                // Move the dice label bounds to the new location
                setPlayerDieBounds(x, y, playerDiceLabel);
                // movePlayerDieToCoords(x, y, playerDiceLabel);
                // Add the player dice label to the board
                bPane.add(playerDiceLabel, new Integer(PLAYER_DICE_LAYER));
            }
        });
    }

    /**
     * Set the icon for the dice label.
     * 
     * @param playerDiceLabel The player dice label
     * @param diceFilename The filename of the dice image
     * @param alpha The transparency of the dice image
     */
    private void setDiceLabelIcon(
        JLabel playerDiceLabel, 
        String diceFilename, 
        float alpha
    ) {
        ImageIcon originalIcon = 
        new ImageIcon(getClass().getClassLoader().getResource("dice/" + 
        diceFilename + ".png"));
        playerDiceLabel.setIcon(originalIcon);
    }

    // /**
    //  * Refresh the icon for a single dice label.
    //  * 
    //  * @param playerLabel The player label
    //  * @param diceFilename The filename of the dice image
    //  */
    // public void refreshSingleDiceLabelIcon(
    //     JLabel playerLabel, 
    //     String diceFilename
    // ) {
    //     // Set the icon for the dice label, 0.7f for 70% transparency
    //     setDiceLabelIcon(playerLabel, diceFilename, 0.70f);
    //     // Repaint the player label
    //     playerLabel.repaint();
    // }

    /**
     * Show the board.
     */
    private void showBoard() {
        // Show the board
        this.setVisible(true);
    }

    /**
     * Add a card to the GUI board.
     * 
     * @param eventData
     */
    @SuppressWarnings("unchecked")
    public void addCard(Object eventData) {
        Map<String, Integer> addCard = (Map<String, Integer>) eventData;
        int cardID = addCard.get("sceneCardID");
        String cardName = String.format("cards/%02d.png", cardID);
        int x = addCard.get("locationX");
        int y = addCard.get("locationY");
        // Add offset to x to compensate for left sidebar
        x += BOARD_OFFSET;
        moveCardToLocation(x, y, cardName, SCENE_CARD_LAYER);
    }

    /**
     * Add card backs to GUI board.
     * 
     * @param eventData
     */
    @SuppressWarnings("unchecked")
    private void addCardBack(Object eventData) {
        // Add card backs to GUI board
        Map<String, Integer> addCardBack = (Map<String, Integer>) eventData;
        int x = addCardBack.get("locationX");
        int y = addCardBack.get("locationY");
        // Add offset to x to compensate for left sidebar
        x += BOARD_OFFSET;
        moveCardToLocation(x, y, "CardBack-small.jpg", SCENE_CARD_COVER_LAYER);
    }

    /**
     * Create player stats table.
     * 
     * @param eventData
     */
    private void createPlayerStats(Object eventData) {
        int numPlayers = (int) eventData;
        playerStatsManager.createPlayerStatsTable(
            bPane, 
            numPlayers, 
            BOARD_IMAGE_WIDTH, 
            PSTATS_ROW_HEIGHT, 
            PSTATS_DICE_COLUMN_WIDTH, 
            PSTATS_HEADER_OFFSET, 
            PSTATS_HEADER_HEIGHT, 
            SIDEBAR_LEFT_BORDER, 
            PSTATS_Y, 
            PSTATS_WIDTH,
            PSTATS_LAYER
        );
    }

    /**
     * Update player stats.
     * 
     * @param eventData
     */
    @SuppressWarnings("unchecked")
    public void updateAllPlayerStats(Object eventData) {
        Map<Integer, List<Integer>> playerData = 
            (Map<Integer, List<Integer>>) eventData;
        // update player stats
        for (Map.Entry<Integer, List<Integer>> entry : playerData.entrySet()) {
            Integer playerID = entry.getKey();
            List<Integer> playerStats = entry.getValue();
            Integer dollars = playerStats.get(0);
            Integer credits = playerStats.get(1);
            Integer rehearsalTokens = playerStats.get(2);
            playerStatsManager.updateStats(
                playerDiceLabels, 
                playerID, 
                dollars, 
                credits, 
                rehearsalTokens
            );        
        }

    }

    /**
     * Handle player move event.
     * 
     * @param eventData
     */
    @SuppressWarnings("unchecked")
    private void handlePlayerMove(Object eventData) {

        // Get the x and y coords of the new location and the player id
        int x = (int) ((Map<String, Object>) eventData).get("locationX");
        int y = (int) ((Map<String, Object>) eventData).get("locationY");
        int playerID = 
            (int) ((Map<String, Object>) eventData).get("playerID");
        // Get the player label
        String key = String.valueOf(playerID);
        JLabel playerLabel = 
            playerDiceLabels.get(key);

        int stophere = 0;
        // Update the dice label location for the player
        if (playerLabel != null) {
            SwingUtilities.invokeLater(() -> {
                movePlayerDieToCoords(
                    x + BOARD_OFFSET + 40 * (playerID - 1), 
                    y + CARD_HEIGHT, 
                    playerLabel
                );
            });
        }

    }

    private void handleLoadGame(Object eventData) {
        // TODO: Handle load game event
    }

    private void handleScoreUpdate(Object eventData) {
        // Handle score update event
        System.out.println("Score updated: " + eventData);
    }

    private void handleGameEvent(Object eventData) {
        // Handle general game event
        System.out.println("Game event occurred: " + eventData);
    }


/************************************************************
 * Observer Pattern Method Utilities
 ************************************************************/

    /**
     * Move a die to a location on the board.
     * 
     * @param x The x-coordinate of the location
     * @param y The y-coordinate of the location
     * @param playerLabel The player label
     */
    public void setPlayerDieBounds(int x, int y, JLabel playerLabel) {
        SwingUtilities.invokeLater(() -> {
            // Move players dice to new location
            playerLabel.setBounds(
                x, 
                y,
                playerLabel.getIcon().getIconWidth(),
                playerLabel.getIcon().getIconHeight()
            );
            // Repaint the player label
            playerLabel.paintImmediately(playerLabel.getBounds());
    });
    }

    /**
     * Move a die to a location on the board.
     * 
     * @param x The x-coordinate of the location
     * @param y The y-coordinate of the location
     * @param playerID The ID of the player
     */
    public void movePlayerDieToCoords(int x, int y, JLabel playerLabel) {
        SwingUtilities.invokeLater(() -> {
            this.showMessage("Current coords: " + playerLabel.getX() + ", " + playerLabel.getY());
            this.showMessage("Moving player die to coords: " + x + ", " + y);
            playerLabel.setBounds(
                x, 
                y,
                playerLabel.getIcon().getIconWidth(),
                playerLabel.getIcon().getIconHeight()
            );
            this.showMessage("Moved! New coords: " + playerLabel.getX() + ", " + playerLabel.getY());
            bPane.revalidate();
            bPane.repaint();
            // playerLabel.revalidate();
            playerLabel.repaint();
            // Revalidate and repaint the parent container (bPane)
            // Container parent = playerLabel.getParent();
            // if (parent != null) {
            //     parent.revalidate();
            //     parent.repaint();
            // }
            int stophere = 1;
        });
    }
    
    /**
     * Move a card to a location on the board.
     * 
     * @param x The x-coordinate of the location
     * @param y The y-coordinate of the location
     * @param filename The filename of the card
     */
    private void moveCardToLocation(int x, int y, String filename, int layer) {
        JLabel card = new JLabel();
        ImageIcon cIcon = 
            new ImageIcon(getClass().getClassLoader().getResource(filename));
        card.setIcon(cIcon);
        card.setBounds(x, y, cIcon.getIconWidth(), cIcon.getIconHeight());
        bPane.add(card, new Integer(layer));
    }

    private void addMouseListenerToArea(Area area) {
        // Add mouse listener to the area
    }

    private void removeMouseListenerFromArea(Area area) {
        // Remove mouse listener from the area
    }

    private void updateInvisibleButtons() {
        // Update the invisible buttons on the board
    }

    public void getLatch() {
        latch.countDown();
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

}  