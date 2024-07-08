import java.util.List;

/*
 * Represents the move action for the player.
 */
public class PlayerActionMove implements PlayerAction {

    /**
     * Validates the move action for the player.
     *
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return true if the player can move, false otherwise
     */
    @Override
    public boolean validate(Player player, GameModel model, GameView view) {
        return checkPlayerHasNoRole(player, view) && checkPlayerHasNotMoved(player, view);
    }

    /**
     * Checks if the player has no role.
     *
     * @param player the player
     * @param view the game view
     * @return true if the player has no role, false otherwise
     */
    private boolean checkPlayerHasNoRole(Player player, GameView view) {
        if (player.getHasRole()) {
            view.showMessage("You must finish your role before moving.");
            return false;
        }
        return true;
    }

    /**
     * Checks if the player has not moved.
     *
     * @param player the player
     * @param view the game view
     * @return true if the player has not moved, false otherwise
     */
    private boolean checkPlayerHasNotMoved(Player player, GameView view) {
        if (player.getHasMoved()) {
            view.showMessage("You have already moved this turn.");
            return false;
        }
        return true;
    }

    /**
     * Executes the move action for the player.
     *
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return true to end turn if player has previously upgraded, false otherwise
     */
    @Override
    public boolean execute(Player player, GameModel model, GameView view) {
        List<String> neighbors = getNeighbors(player);
        displayMoveOptions(neighbors, view);
        String location = getPlayerLocationChoice(view);
        String locationFormatted = formatLocationForNeighbors(location);
        if (!isValidMove(neighbors, locationFormatted)) {
            view.showMessage("Invalid location.");
            return false;
        }
        movePlayerToLocation(player, model, view, location);
        player.setHasMoved(true);
        return player.getHasUpgraded();
    }

    /**
     * Gets the neighbors of the player's current location.
     *
     * @param player the player
     * @return the neighbors of the player's current location
     */
    private List<String> getNeighbors(Player player) {
        return player.getLocation().getNeighbors();
    }

    /**
     * Displays the move options for the player.
     *
     * @param neighbors the neighbors of the player's current location
     * @param view the game view
     */
    private void displayMoveOptions(List<String> neighbors, GameView view) {
        view.showMessage("Where would you like to move?");
        for (String neighbor : neighbors) {
            view.showMessage(formatLocationForDisplay(neighbor));
        }
    }

    /**
     * Gets the player's location choice.
     *
     * @param view the game view
     * @return the player's location choice
     */
    private String getPlayerLocationChoice(GameView view) {
        return view.getPlayerInput();
    }

    /**
     * Formats the location for the model.
     *
     * @param location the location
     * @return the formatted location
     */
    private String formatLocationForNeighbors(String location) {
        switch (location) {
            case "Trailer":
                return "trailer";
            case "Casting Office":
                return "office";
            default:
                return location;
        }
    }

    /**
     * Checks if the move is valid.
     *
     * @param neighbors the neighbors of the player's current location
     * @param locationFormatted the formatted location to move to
     * @return true if the move is valid, false otherwise
     */
    private boolean isValidMove(List<String> neighbors, String locationFormatted) {
        return neighbors.contains(locationFormatted);
    }

    /**
     * Moves the player to the location.
     *
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @param location the location to move to
     */
    private void movePlayerToLocation(Player player, GameModel model, GameView view, String endLocationString) {
        // Get String representation of start location
        String startLocationString = player.getLocation().getName();
        // Get startLocation location
        Location startLocation = model.getBoard().getLocations().get(startLocationString);
        // Remove player from startLocation
        startLocation.removePlayer(player);
        // get endLocation location
        Location endLocation = model.getBoard().getLocations().get(endLocationString);
        // Add selected location to the player
        player.setLocation(endLocation);
        // Add player to the new location
        endLocation.addPlayer(player);
        view.showMessage(startLocationString + " -> " + endLocationString);
    }

    /**
     * Formats the location for display.
     *
     * @param location the location
     * @return the formatted location
     */
    private String formatLocationForDisplay(String location) {
        switch (location) {
            case "trailer":
                return "Trailer";
            case "office":
                return "Casting Office";
            default:
                return location;
        }
    }

}
