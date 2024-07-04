import java.util.List;

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
        // Check if player has role
        if (player.getHasRole()) {
            view.showMessage("You must finish your role before moving.");
            return false;
        }
        // Check if player has moved
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
        // Get the list of neighbors for the players location
        List<String> neighbors = player.getLocation().getNeighbors();
        // Display the list of options
        view.showMessage("Where would you like to move?");
        for (String neighbor : neighbors) {
            if ("trailer".equals(neighbor)) {
                view.showMessage("Trailer");
            } else if ("office".equals(neighbor)) {
                view.showMessage("Casting Office");
            } else {
                view.showMessage(neighbor);
            }
        }
        // Get the location from the player
        String location = view.getPlayerInput();
        if (!neighbors.contains(location)) {
            view.showMessage("Invalid location.");
            return false;
        }
        // Save the start location
        String startLocation = player.getLocation().getName();
        // Move the player to the new location and add them to the Location's player list
        player.setLocation(model.getBoard().getLocations().get(location));
        player.getLocation().addPlayer(player);
        // Save the end location
        String endLocation = player.getLocation().getName();
        // Show the player has moved
        view.showMessage(startLocation + " -> " + endLocation);
        // set player has moved
        player.setHasMoved(true);
        // Check if the player has already upgraded during their turn
        if (player.getHasUpgraded()) {
            return true; // End the turn if the player has already upgraded
        }
        return false;
    }

}
