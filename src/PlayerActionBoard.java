import java.util.Comparator;
import java.util.List;

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
        // Get list of players
        List<Player> players = model.getPlayers();
        // Show message
        view.showMessage("Board:");
        // Sort players by ID
        players.sort(Comparator.comparing(Player::getID));
        // iterate through players
        for (Player p : players) {
            String locationName = p.getLocation().getName();
            // Construct player info string with rank, money, credits, and rehearsal tokens
            String playerInfo = String.format("Player %d (rank %d, $%d, %dcr, %drt) - %s", 
                                              p.getID(), 
                                              p.getRank(), 
                                              p.getMoney(), 
                                              p.getCredits(), 
                                              p.getRehearsalTokens(), 
                                              locationName
            );
            // Check if the location is not "Trailer" or "Casting Office" before showing shots remaining
            if (!locationName.equals("Trailer") && !locationName.equals("Casting Office")) {
                if (!p.getLocation().isWrapped()) {
                    int shotsRemaining = p.getLocation().getShots();
                    playerInfo += " - Shots remaining: " + shotsRemaining;
                } else {
                    playerInfo += " - Scene wrapped";
                }
            }
            // If the player is active, prepend with an asterisk
            if (p.getActive()) {
                view.showMessage("* " + playerInfo);
            } else {
                view.showMessage("  " + playerInfo);
            }
        }
        return false;
    }

}
