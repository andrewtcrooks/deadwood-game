public class PlayerActionRehearse implements PlayerAction {

    /**
     * Validates the rehearse action for the player.
     *  
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return true if the player has a role, false otherwise
     */
    @Override
    public boolean validate(Player player, GameModel model, GameView view) {
        // Check if player has a role
        if (!player.getHasRole()) {
            view.showMessage("You do not have a role to rehearse.");
            return false;
        }
        return true;
    }

    /**
     * Executes the rehearse action for the player.
     *  
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return always true to end player turn
     */
    @Override
    public boolean execute(Player player, GameModel model, GameView view) {
        // Add a rehearsal token and display message
        player.incrementRehearsalTokens();
        view.showMessage("success! You got a rehearsal token!");
        return true;
    }

}
