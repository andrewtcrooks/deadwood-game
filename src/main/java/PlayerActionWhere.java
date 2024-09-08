/*
 * Represents the where action for the player.
 */
public class PlayerActionWhere implements PlayerAction {

    /**
     * Constructs a PlayerActionWhere object.
     */
    public PlayerActionWhere() {
        super();
    }

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
        Deck deck = model.getDeck();
        Board board = model.getBoard();
        String locationName = board.getPlayerLocationName(player);
        Location location = model.getLocation(locationName);
        // Check if the scene is wrapped first
        if (location.getIsWrapped()) {
            view.showMessage(locationName + " wrapped");
        } else if ("Trailer".equals(locationName)) {
            view.showMessage("You are still in your Trailer!");
        } else if ("Casting Office".equals(locationName)) {
            view.showMessage("You are at the Casting Office");
        } else {
            int sceneCardID = board.getLocationSceneCardID(locationName);
            SceneCard card = deck.getDrawnCard(sceneCardID);
            String sceneTitle = card.getTitle();
            view.showMessage(locationName + " shooting " + sceneTitle + " (scene " + sceneCardID + ")");
        }
        return false;
    }

}
