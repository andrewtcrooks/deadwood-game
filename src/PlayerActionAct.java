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
        // Check if player has a role
        if (board.getPlayerRole(player) == null) {
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
        Board board = model.getBoard();
        int budget = getSceneBudget(player, board);
        int roll = performDiceRoll(player);
        // Check if the player succeeded
        if (roll >= budget) {
            processSuccess(player, roll, board, model, view);
        } else {
            processFailure(player,roll, board, view);
        }
        // Check if the scene is ready to be wrapped
        if (noShotsRemain(player, board, model)) {
            wrapLocationScene(player, board, model, view);
        }
        return true;
    }

    /**
     * Gets the budget of the scene the player is acting in.
     *  
     * @param player the player
     * @return the budget of the scene
     */
    private int getSceneBudget(Player player, Board board) {
        String locationName = board.getPlayerLocationName(player);
        return board.getLocationSceneCard(locationName).getBudget();
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
    private void processSuccess(Player player, int roll, Board board, GameModel model, GameView view) {
        view.showMessage("You rolled a " + roll + ". Success!");
        String locationName = board.getPlayerLocationName(player);
        Location location = model.getLocation(locationName);
        location.removeShotCounter();
        // Check if the player is on-card
        if (board.getPlayerRole(player).getOnCard()) {
            player.addCredits(2);
        } else {
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
    private void processFailure(Player player, int roll, Board board, GameView view) {
        view.showMessage("You rolled a " + roll + ". Failure.");
        // Check if the player is on-card
        if (!board.getPlayerRole(player).getOnCard()) {
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
    private void wrapLocationScene(Player player, Board board, GameModel model, GameView view) {
        String locationName = board.getPlayerLocationName(player);
        Location location = model.getLocation(locationName);
        List<Player> players = model.getPlayers();
        // Check if any player has an on-card role
        boolean anyPlayerOnCard = board.getLocationPlayers(players, location).stream()
            .anyMatch(p -> {
                Role role = board.getPlayerRole(p);
                return role != null && role.getOnCard(); // Check if player has a non-null on-card role
            });
        // Display bonus payout message if there are any players on-card
        if (anyPlayerOnCard) {
            view.showMessage("Bonus payout!");
        }
        board.wrapScene(players, location);
        view.showMessage("The scene is wrapped.");
    }

}
