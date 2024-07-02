public class PlayerActionWhere implements PlayerAction {

    /**
     * Validates the where action for the player.
     *
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return always true to validate where action
     */
    @Override
    public boolean validate(Player player, GameModel model, GameView view) {
        return true;
    }

    /**
     * Executes the where action for the player.
     *
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return always false to prevent player turn end
     */
    @Override
    public boolean execute(Player player, GameModel model, GameView view) {
        // Display the player location and scene details
        if (player.getLocation().getName().equals("Trailer") ||
            player.getLocation().getName().equals("Casting Office")) {
            view.displayPlayerLocation(
                player.getLocation().getName()
            );
        }else{
            view.displayPlayerLocation(
                player.getLocation().getName(),
                player.getLocation().isWrapped(),
                player.getLocation().getSceneCard().getTitle(),
                player.getLocation().getSceneCard().getID()
            );
        }
        return false;
        }

}
