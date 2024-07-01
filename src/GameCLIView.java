import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class GameCLIView implements GameView {
    private static GameCLIView instance;
    private Scanner scanner;

    /**
     * Constructs a new GameCLIView.
     */
    private GameCLIView() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Singleton instance of the GameCLIView.
     */
    public static synchronized GameCLIView getInstance() {
        if (instance == null) {
            instance = new GameCLIView();
        }
        return instance;
    }

    // TODO: comment out unless running Unit Tests
    /**
     * Resets the instance of the GameCLIView.
     */
    public static synchronized void reset() {
        instance = null;
    }

    /**
     * Updates the view.
     * 
     * @param arg The argument to update the view
     */
    public void update(Object arg) {
        // TODO: complete this stub
    }
    
    /**
     * Display a message.
     * 
     * @param message The message to display
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    public void displayTurn(Player player) {
        System.out.println("It's Player " + player.getID() + "'s turn.");
    }

    /**
     *  Get the number of players.
     */
    @Override
    public int getNumPlayers() {
        while (true) {
            System.out.println("Enter the number of players (between 2 and 8):");
            try {
                int numPlayers = scanner.nextInt();
                scanner.nextLine(); // consume the newline
                if (numPlayers >= 2 && numPlayers <= 8) {
                    return numPlayers;
                } else {
                    System.out.println("Invalid number of players.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter an integer.");
                scanner.nextLine(); // discard the invalid input
            }
        }
    }

    /**
     * Process the player's command.
     * 
     * The player can enter one of the following commands:
     * <ul>
     * <li>who: Display player info</li>
     * <li>where: Display player location</li>
     * <li>board: Display all player locations and indicate the active player</li>
     * <li>move: Move player to a new location</li>
     * <li>work: Player works on a part</li>
     * <li>upgrade: Player upgrades rank</li>
     * <li>rehearse: Player rehearses</li>
     * <li>act: Player acts on a role</li>
     * <li>end: End player's turn</li>
     * <li>quit: Quit the game</li>
     * <li>save: Save the game</li>
     * <li>load: Load the game</li>
     * <li>help: Display player options</li>
     * </ul>
     * 
     * @param command The command entered by the player
     * @return The player input
     */
    @Override
    public String getPlayerInput() {
        String command = scanner.nextLine();
        return command;
    }

    /**
     * Displays the welcome message.
     */
    public void displayWelcomeMessage() {
        System.out.println("Welcome to Deadwood! Let's play!");
    }

    /**
     * Displays Invalid Command.
     */
    @Override
    public void displayInvalidCommand() {
        System.out.println("Invalid command.");
    }

    /**
     * Displays Invalid Command and allow Reentry.
     */
    @Override
    public void displayInvalidCommandReenter() {
        System.out.println("Invalid command. Please enter a valid command.");
    }


    /**
     * Displays Invalid Location.
     */
    @Override
    public void displayInvalidLocation() {
        System.out.println("Invalid location.");
    }

    /**
     * Displays Invalid Role.
     */
    @Override
    public void displayInvalidRole() {
        System.out.println("Invalid role.");
    }

    /**
     * Displays Invalid Rank.
     */
    @Override
    public void displayPlayerRankTooLow(int playerRank, int roleRank) {
        System.out.println("Your rank (" + playerRank + ") is too low to" + 
            " assume this role (requires rank " + roleRank + ").");
    }

    /**
     * Displays Invalid Upgrade.
     */
    @Override
    public void displayInvalidUpgrade() {
        System.out.println("You cannot afford to upgrade to this rank.");
    }

    /**
     * Displays the player's information.
     * 
     * @param player
     */
    @Override
    public void displayPlayerInfo(int id, int rank, int money, int credits, int rehearsalTokens) {
        System.out.println("Player " + id +
                    " (rank " + rank + 
                    ", $" + money + 
                    ", " + credits + "cr, " + 
                    rehearsalTokens + "rt) "
        );
    }

    /**
     * Displays the player's location.
     * 
     * @param locationName
     */
    @Override
    public void displayPlayerLocation(String locationName) {
        if ("trailer".equalsIgnoreCase(locationName)) {
            System.out.println("You are still in your Trailer!");
        } else if ("office".equalsIgnoreCase(locationName)){
            System.out.println("You are at the Casting Office");
        }
    }

    /**
     * Displays the player's location.
     * 
     * @param locationName
     * @param isSceneWrapped
     * @param sceneTitle
     * @param sceneID
     */
    @Override
    public void displayPlayerLocation(String locationName, boolean isSceneWrapped, String sceneTitle, int sceneID) {
        if ("trailer".equalsIgnoreCase(locationName)) {
            System.out.println("You are still in your Trailer!");
        } else if ("office".equalsIgnoreCase(locationName)){
            System.out.println("You are at the Casting Office");
        } else if (isSceneWrapped) {
            System.out.println(locationName + " wrapped");
        } else {
            System.out.println(locationName + " shooting " + sceneTitle + " (scene " + sceneID + ")");
        }
    }

    /**
     * Displays the board.
     * 
     * @param players
     */
    @Override
    public void displayBoard(List<Player> players) {
        System.out.println("Board:");
    
        // Sort players by ID
        players.sort(Comparator.comparing(Player::getID));
    
        for (Player player : players) {
            String locationName = player.getLocation().getName();
            String playerInfo = "Player " + player.getID() + " - " + locationName;
    
            // Check if the location is not "trailer" or "office" before showing shots remaining
            if (!locationName.equalsIgnoreCase("trailer") && !locationName.equalsIgnoreCase("office")) {
                int shotsRemaining = player.getLocation().getShots();
                if (shotsRemaining > 0) {
                    playerInfo += " - Shots remaining: " + shotsRemaining;
                } else {
                    playerInfo += " - Scene wrapped";
                }
            }
    
            // If the player is active, prepend with an asterisk
            if (player.getActive()) {
                System.out.println("* " + playerInfo);
            } else {
                System.out.println("  " + playerInfo);
            }
        }
    }

    /**
     * Displays the player's move options.
     * 
     * @param neighbors
     */
    @Override
    public void displayPlayerMoveOptions(List<String> neighbors) {
        System.out.println("Where would you like to move?");
        for (String neighbor : neighbors) {
            System.out.println(neighbor);
        }
    }

    /**
     * Displays the player's move from one location to another.
     * 
     * @param startLocation The starting location of the player.
     * @param endLocation The location the player moved to.
     */
    public void displayPlayerMove(String startLocation, String endLocation) {
        System.out.println(startLocation + " -> " + endLocation);
    }

    /**
     * Displays that the player has already moved.
     */
    @Override
    public void displayPlayerAlreadyMoved() {
        System.out.println("You have already moved this turn.");
    }

    /**
     * Displays that the player cannot move.
     */
    @Override
    public void displayPlayerCannotMove() {
        System.out.println("You must finish your role before moving.");
    }

    /**
     * Displays the player's upgrade options.
     * 
     * @param player
     */
    @Override
    public void displayUpgradeOptions(Player player) {
        if (!player.getLocation().getName().equals("office")) {
            System.out.println("Location is not Casting Office. Cannot upgrade.");
        } else {
            int currentRank = player.getRank();
            System.out.println("You can upgrade to one of the following ranks:");
            for (int i = currentRank + 1; i <= 6; i++) {
                System.out.println(i);
            }
        }
    }

    /**
     * Displays the player's payment options.
     * 
     * @param player
     */
    public void displayDualPaymentOptions(Player player) {
        System.out.println("You have enough to pay with either dollars or credits:\n" +
            "Your balance: $" + player.getMoney() + ", " +
            player.getCredits() + " credits.");
    }

    /**
     * Displays that the player is not in the Casting Office.
     * 
     * @param player
     */
    public void displayNotInOfficeMessage(Player player) {
        System.out.println("You are not in the Casting Office.");
    }

    /**
     * Displays the player's upgrade success.
     * 
     * @param chosenRank
     */
    @Override
    public void displayUpgradeSuccess(int chosenRank) {
        System.out.println("You have successfully upgraded to rank " + chosenRank + ".");
    }

    /**
     * Displays the player's upgrade failure.
     */
    @Override
    public void displayUpgradeFailure() {
        System.out.println("You cannot afford to upgrade to this rank.");
    }

    /**
     * Displays that the player has already upgraded.
     */
    @Override
    public void displayPlayerAlreadyUpgraded() {
        System.out.println("You have already upgraded this turn.");
    }

    /**
     * Displays the player's role.
     * 
     * @param player
     */
    @Override
    public void displayPlayerRole(Player player) {
        System.out.println(player.getRole().getName());
    }

    /**
     * Displays the player's work options based on the locations available roles
     * 
     * @param player
     */
    @Override
    public void displayWorkOptions(Player player) {
        if (player.getLocation().isWrapped()) {
            System.out.println("Location is wrapped. No more work available until tomorrow.");
        } else {
            System.out.println("You can work on one of the following roles:");
            List<Role> allRoles = new ArrayList<>();
            // Combine roles into one list
            for (Role role : player.getLocation().getRoles()) {
                if (!role.isOccupied()) {
                    allRoles.add(role);
                }
            }
            // Sort roles by rank, then by "for scale"
            allRoles.sort((role1, role2) -> {
                int rankCompare = Integer.compare(role1.getRank(), role2.getRank());
                if (rankCompare == 0) {
                    // If ranks are equal, sort by "for scale" (false first)
                    return Boolean.compare(role1.getOnCard(), role2.getOnCard());
                }
                return rankCompare;
            });
            // Print roles with rank and "for scale" tag as needed
            for (Role role : allRoles) {
                System.out.print(role.getName());
                System.out.print("  Rank: " + role.getRank());
                if (!role.getOnCard()) {
                    System.out.println(" (for scale)");
                } else {
                    System.out.println();
                }
            }
        }
    }

    /**
     * Displays that the player has already has a role.
     */
    @Override
    public void displayPlayerAlreadyHasRole(Player player) {
        System.out.println(player.getRole().getName());
    }

    /**
     * Displays that the player has already has a role.
     */
    @Override
    public void displayPlayerHasNoRole() {
        System.out.println("You don't have a role to act.");
    }

    /**
     * Displays the player's rehearsal options.
     * 
     * @param player
     */
    public void displayPlayerRehearsed(Player player) {
        System.out.println("success! You got a rehearsal token!");
    }

    /**
     * Displays the player's act options.
     * 
     * @param player
     */
    @Override
    public void displayAct(Player player) {
        // TODO:

    }

    /**
     * Displays the player's act success
     */
    @Override
    public void displayActSuccess(int roll) {
        System.out.println("You rolled a " + roll + ". Success!");
    }

    /**
     * Displays the player's act success
     */
    @Override
    public void displayActFail(int roll) {
        System.out.println("You rolled a " + roll + ". Failure.");
    }

    /**
     * Displays that the scene is wrapped.
     */
    @Override
    public void displaySceneIsWrapped() {
        System.out.println("The scene is wrapped.");
    }

    /**
     * Displays the player's saveed game options.
     */
    public void displaySaveGameCurrentMessage() {
        System.out.println("Current saved games");
    }

    /**
     * Displays the player's end options.
     */
    public void displaySaveGameFiles() {
        // print the filenames of all .savegame files in the current directory
        try (Stream<Path> paths = Files.walk(Paths.get("."))) {
            paths
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .filter(file -> file.getName().endsWith(".savegame"))
                .forEach(file -> {
                    String filename = file.getName();
                    // clip off the .savegame extension
                    String trimmedFilename = filename.substring(0, filename.length() - ".savegame".length());
                    System.out.println(trimmedFilename);
                });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays the player's save game options.
     */
    public void displaySaveGameInputMessage() {
        System.out.println("Enter a name for the new saved game (must be unique):");
    }

    /**
     * Displays the player's save game must be unique.
     */
    public void displaySaveGameMustBeUnique() {
        System.out.println("Save game name must be unique.");
    }

    /**
     * Displays the player's save game does not exist.
     */
    public void displaySaveGameDoesNotExist() {
        System.out.println("Save game does not exist.");
    }

    /**
     * Displays the player's load game options.
     */
    public void displayLoadGameInputMessage() {
        System.out.println("Enter the name of the saved game to load (choose from list):");
    }

    /**
     * Displays the player options.
     */
    public void displayPlayerOptions() {
        System.out.println("You can enter one of the following commands:");
        System.out.println("who: Display player info");
        System.out.println("where: Display player location");
        System.out.println("board: Display all player locations and indicate the active player");
        System.out.println("move: Move player to a new location");
        System.out.println("work: Player works on a part");
        System.out.println("upgrade: Player upgrades rank");
        System.out.println("rehearse: Player rehearses");
        System.out.println("act: Player acts on a role");
        System.out.println("end: End player's turn");
        System.out.println("quit: Quit the game");
        System.out.println("save: Save the game");
        System.out.println("load: Load the game");
        System.out.println("help: Display player options");
    }

}