/*
 * Represents the who action for the player.
 */
public class PlayerActionWho implements PlayerAction{

    /**
     * Validates the who action for the player.
     *
     * @param player the player to who
     * @param model the game model
     * @param view the game view
     * @return always true to validate who action
     */
    @Override
    public boolean validate(Player player, GameModel model, GameView view) {
        return true;
    }

    /**
     * Executes the who action for the player.
     *
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return always false to prevent player turn end
     */
    @Override
    public boolean execute(Player player, GameModel model, GameView view) {
        // Display the player info
        view.showMessage("Player " + player.getID() +
                         " (rank " + player.getRank() + 
                         ", $" + player.getMoney() + 
                         ", " + player.getCredits() + "cr, " + 
                         player.getRehearsalTokens() + "rt) "
        );
        return false;
    }

}
