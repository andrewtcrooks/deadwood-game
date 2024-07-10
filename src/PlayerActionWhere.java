/*
 * Represents the where action for the player.
 */
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
        Board board = model.getBoard();
        Location location = board.getPlayerLocation(player);
        String locationName = board.getPlayerLocationName(player);
        // Check if the scene is wrapped first
        if (location.getIsWrapped()) {
            view.showMessage(locationName + " wrapped");
        } else if ("Trailer".equals(locationName)) {
            view.showMessage("You are still in your Trailer!");
        } else if ("Casting Office".equals(locationName)) {
            view.showMessage("You are at the Casting Office");
        } else {
            String sceneTitle = location.getSceneCard().getTitle();
            int sceneID = location.getSceneCard().getID();
            view.showMessage(locationName + " shooting " + sceneTitle + " (scene " + sceneID + ")");
        }
        return false;
    }

}
