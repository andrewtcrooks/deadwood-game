public class PlayerActionRehearse implements PlayerAction {

    /**
     * Validates the rehearse action for the player.
     *  
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return always returns true to validate rehearse action
     */
    @Override
    public boolean validate(Player player, GameModel model, GameView view) {
        return true;
    }

    /**
     * Executes the rehearse action for the player.
     *  
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return true to end turn if the player was successfully rehearses, false otherwise
     */
    @Override
    public boolean execute(Player player, GameModel model, GameView view) {
        // Check if player has a role
        if (!player.getHasRole()) {
            view.displayPlayerHasNoRole();
            return false;
        }

        // If player has a role, add a rehearsal token and display message
        player.incrementRehearsalTokens();
        view.displayPlayerRehearsed(player);

        return true;
    }

}
