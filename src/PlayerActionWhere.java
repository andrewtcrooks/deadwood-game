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
        // Get the player location name
        String locationName = player.getLocation().getName();
        // Display the player location and scene details
        if ("Trailer".equals(locationName)) {
            view.showMessage("You are still in your Trailer!");
        } else if ("Casting Office".equals(locationName)) {
            view.showMessage("You are at the Casting Office");
        } else {
            String sceneTitle = player.getLocation().getSceneCard().getTitle();
            int sceneID = player.getLocation().getSceneCard().getID();
            if (player.getLocation().isWrapped()) {
                view.showMessage(locationName + " wrapped");
            } else {
                view.showMessage(locationName + " shooting " + sceneTitle + " (scene " + sceneID + ")");
            }
        }
        return false;
    }

}
