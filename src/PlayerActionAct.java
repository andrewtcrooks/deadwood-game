import java.util.List;

/*
 * Represents the act action for the player.
 */
public class PlayerActionAct implements PlayerAction {

    /**
     * Validates the act action for the player.
     *  
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return true if the player has a role, false otherwise
     */
    @Override
    public boolean validate(Player player, GameModel model, GameView view) {
        Board board = model.getBoard();
        int playerID = player.getID();
        // Check if player has a role
        if (board.getPlayerRole(playerID) == null) {
            view.showMessage("You do not have a role to act.");
            return false;
        }
        return true;
    }

    /**
     * Executes the act action for the player.
     *  
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return always true to end player turn
     */
    @Override
    public boolean execute(Player player, GameModel model, GameView view) {
        Deck deck = model.getDeck();
        Board board = model.getBoard();
        Location location = model.getLocation(board.getPlayerLocationName(player));
        int budget = getSceneBudget(player, deck, board, model);
        int roll = performDiceRoll(player);
        // Check if the player succeeded
        if (roll >= budget) {
            processSuccess(player, roll, deck, board, model, view);
            location.removeShotCounter();
        } else {
            processFailure(player,roll, deck, board, model, view);
        }
        // Check if the scene is ready to be wrapped
        if (noShotsRemain(player, board, model)) {
            wrapLocationScene(player, location, deck, board, model, view);
        }
        return true;
    }

    /**
     * Gets the budget of the scene the player is acting in.
     *  
     * @param player the player
     * @return the budget of the scene
     */
    private int getSceneBudget(Player player, Deck deck, Board board, GameModel model) {
        String locationName = board.getPlayerLocationName(player);
        Integer sceneCardID = board.getLocationSceneCardID(locationName);
        SceneCard sceneCard = deck.getDrawnCard(sceneCardID);
        return sceneCard.getBudget();
    }

    /**
     * Performs a dice roll for the player.
     *  
     * @param player the player
     * @return the result of the dice roll
     */
    private int performDiceRoll(Player player) {
        Dice dice = new Dice();
        dice.roll();
        return player.getRehearsalTokens() + dice.getValue();
    }

    /**
     * Processes a successful act action for the player.
     *  
     * @param player the player
     * @param roll the result of the dice roll
     * @param board the game board
     * @param model the game model
     * @param view the game view
     */
    private void processSuccess(Player player, int roll, Deck deck, Board board, GameModel model, GameView view) {
        view.showMessage("You rolled a " + roll + ". Success!");
        String locationName = board.getPlayerLocationName(player);
        Integer sceneCardID = board.getLocationSceneCardID(locationName);
        SceneCard sceneCard = deck.getDrawnCard(sceneCardID);
        // Check if player has a role that matches a role on the SceneCard
        if (sceneCard.getRoles().stream().anyMatch(r -> r.getName().equals(board.getPlayerRole(player.getID())))) {
            player.addCredits(2);
        } else if (board.getPlayerRole(player.getID()) != null) { //check if player has a role (that must be off card))
            player.addMoney(1);
            player.addCredits(1);
        }
    }

    /**
     * Processes a failed act action for the player.
     *  
     * @param player the player
     * @param roll the result of the dice roll
     * @param board the game board
     * @param view the game view
     */
    private void processFailure(Player player, int roll, Deck deck, Board board, GameModel model, GameView view) {
        view.showMessage("You rolled a " + roll + ". Failure.");
        String locationName = board.getPlayerLocationName(player);
        Integer sceneCardID = board.getLocationSceneCardID(locationName);
        SceneCard sceneCard = deck.getDrawnCard(sceneCardID);
        // Check if the player is off-card
        if (!sceneCard.getRoles().stream().anyMatch(r -> r.getName().equals(board.getPlayerRole(player.getID())))) {
            player.addMoney(1);
        }
    }

    /**
     * Checks if no shots remain at the player's location.
     * 
     * @param player the player
     * @param board the game board
     * @param model the game model
     * @return true if the scene is ready to be wrapped, false otherwise
     */
    private boolean noShotsRemain(Player player, Board board, GameModel model) {
        String locationName = board.getPlayerLocationName(player);
        Location location = model.getLocation(locationName);
        if (location.getShots() == 0) {
            return true;
        }
        return false;
    }

    /**
     * Wraps the scene at the player's location.
     * 
     * @param player the player
     * @param board the game board
     * @param model the game model
     * @param view the game view
     */
    private void wrapLocationScene(Player player, Location location, Deck deck, Board board, GameModel model, GameView view) {
        String locationName = location.getName();
        // Get all players
        List<Player> players = model.getPlayers();
        // Get just the players at the location
        List<Player> playersAtLocation = players.stream()
                .filter(p -> board.getPlayerLocationName(p).equals(locationName))
                .toList();
        // Check if any player at the location has an on-card role
        boolean anyPlayerOnCard = playersAtLocation.stream().anyMatch(p -> board.getLocationSceneCardRoles(locationName, deck).stream()
                .anyMatch(r -> r.getName().equals(board.getPlayerRole(p.getID()))));
        // Display bonus payout message if there are any players on-card
        if (anyPlayerOnCard) {
            view.showMessage("Bonus payout!");
        }
        board.wrapScene(playersAtLocation, deck, location);
        view.showMessage("The scene is wrapped.");
    }

}
