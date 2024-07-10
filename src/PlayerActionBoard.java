import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/*
 * Represents the board action for the player.
 */
public class PlayerActionBoard implements PlayerAction {

    /**
     * Validates the board action for the player.
     *  
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return always true to validate board action
     */
    @Override
    public boolean validate(Player player, GameModel model, GameView view) {
        return true;
    }

    /**
     * Executes the board action for the player.
     *  
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return always false to prevent player turn end
     */
    @Override
    public boolean execute(Player player, GameModel model, GameView view) {
        Board board = model.getBoard();
        List<Player> players = getSortedPlayers(model);
        displayBoard(players, board, view);
        return false;
    }

    /**
     * Returns the sorted list of players.
     * 
     * @param model the game model
     * @return the sorted list of players
     */
    private List<Player> getSortedPlayers(GameModel model) {
        List<Player> players = new ArrayList<>(model.getPlayers());
        players.sort(Comparator.comparing(Player::getID));
        return players;
    }

    /**
     * Displays the board with the player information.
     * 
     * @param players the players
     * @param view the game view
     */
    private void displayBoard(List<Player> players, Board board, GameView view) {
        view.showMessage("Board:");
        displayWrappedLocations(players, board, view);
        for (Player p : players) {
            displayPlayerInfo(p, board, view);
        }
    }

    /**
     * Displays the wrapped locations on the board.
     * 
     * @param players the players
     * @param view the game view
     */
    private void displayWrappedLocations(List<Player> players, Board board, GameView view) {
        Set<String> wrappedLocations = board.getLocations().values().stream().filter(Location::getIsWrapped)
        .map(Location::getName)
        .collect(Collectors.toSet());
        if (!wrappedLocations.isEmpty()) {
            view.showMessage("Wrapped Locations: " + String.join(", ", wrappedLocations));
        }
    }

    /**
     * Displays the player information on the board.
     * 
     * @param player the player
     * @param view the game view
     */
    private void displayPlayerInfo(Player player, Board board, GameView view) {
        String playerInfo = constructPlayerInfo(player, board);
        if (player.getActive()) {
            view.showMessage("* " + playerInfo);
        } else {
            view.showMessage("  " + playerInfo);
        }
    }

    /**
     * Constructs the player information to be displayed on the board.
     * 
     * @param player the player
     * @return the player information
     */
    private String constructPlayerInfo(Player player, Board board) {
        String locationName = board.getPlayerLocationName(player);
        String playerInfo = String.format("Player %d (rank %d, $%4d, %4dcr, %drt) - %14s", 
                                          player.getID(), 
                                          player.getRank(), 
                                          player.getMoney(), 
                                          player.getCredits(), 
                                          player.getRehearsalTokens(), 
                                          locationName);

        if (!locationName.equals("Trailer") && !locationName.equals("Casting Office")) {
            playerInfo += getLocationStatus(player, board);
        }

        return playerInfo;
    }

    /**
     * Returns the status of the player's location.
     * 
     * @param player the player
     * @return the status of the player's location
     */
    private String getLocationStatus(Player player, Board board) {
        Location location = board.getPlayerLocation(player);
        if (!location.getIsWrapped()) {
            int shotsRemaining = location.getShots();
            return " - Shots remaining: " + shotsRemaining;
        } else {
            return " - Scene wrapped";
        }
    }

}
