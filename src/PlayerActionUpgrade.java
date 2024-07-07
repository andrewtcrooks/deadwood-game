import java.util.HashMap;
import java.util.Map;

/**
 * Represents the upgrade action for the player.
 */
public class PlayerActionUpgrade implements PlayerAction {
    // Dollar costs for each rank
    private static final Map<Integer, Integer> dollarCosts = new HashMap<>();
    // Credit costs for each rank
    private static final Map<Integer, Integer> creditCosts = new HashMap<>();
    // Initialize dollar costs and credit costs for each rank
    static {
        dollarCosts.put(2, 4); // $4 for rank 2
        dollarCosts.put(3, 10); // $10 for rank 3
        dollarCosts.put(4, 18); // $18 for rank 4
        dollarCosts.put(5, 28); // $28 for rank 5
        dollarCosts.put(6, 40); // $40 for rank 6
        creditCosts.put(2, 5); // 5 credits for rank 2
        creditCosts.put(3, 10); // 10 credits for rank 3
        creditCosts.put(4, 15); // 15 credits for rank 4
        creditCosts.put(5, 20); // 20 credits for rank 5
        creditCosts.put(6, 25); // 25 credits for rank 6
    }

    /**
     * Validates the upgrade action for the player.
     *
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return true if the player can upgrade, false otherwise
     */
    @Override
    public boolean validate(Player player, GameModel model, GameView view) {
        return isAtCastingOffice(player, view) && // true if at Casting Office
               hasNotAlreadyUpgraded(player, view) && // true if not already upgraded
               isNotAtHighestRank(player, view) && // true if not at highest rank
               canAffordNextRank(player, view); // true if can afford next rank
    }
    
    /**
     * Checks if the player is at the Casting Office.
     * 
     * @param player the player
     * @param view the game view
     * @return true if the player is at the Casting Office, false otherwise
     */
    private boolean isAtCastingOffice(Player player, GameView view) {
        if (!"Casting Office".equals(player.getLocation().getName())) {
            view.showMessage("You must be at the Casting Office to upgrade.");
            return false;
        }
        return true;
    }
    
    /**
     * Checks if the player has already upgraded.
     * 
     * @param player the player
     * @param view the game view
     * @return true if the player has not already upgraded, false otherwise
     */
    private boolean hasNotAlreadyUpgraded(Player player, GameView view) {
        if (player.getHasUpgraded()) {
            view.showMessage("You have already upgraded this turn.");
            return false;
        }
        return true;
    }

    /**
     * Checks if the player is at the highest rank.
     * 
     * @param player the player
     * @param view the game view
     * @return true if the player is at the highest rank, false otherwise
     */
    private boolean isNotAtHighestRank(Player player, GameView view) {
        if (player.getRank() == 6) {
            view.showMessage("You are already at the highest rank.");
            return false;
        }
        return true;
    }
    
    /**
     * Checks if the player can afford the next rank.
     * 
     * @param player the player
     * @param view the game view
     * @return true if the player can afford the next rank, false otherwise
     */
    private boolean canAffordNextRank(Player player, GameView view) {
        int nextRank = player.getRank() + 1;
        int dollarCostForNextRank = dollarCosts.getOrDefault(nextRank, Integer.MAX_VALUE);
        int creditCostForNextRank = creditCosts.getOrDefault(nextRank, Integer.MAX_VALUE);
        if (player.getMoney() < dollarCostForNextRank && player.getCredits() < creditCostForNextRank) {
            view.showMessage("You do not have enough money or credits to upgrade to a higher rank.");
            return false;
        }
        return true;
    }

    /**
     * Executes the upgrade action for the player.
     *
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return true to end turn if player has previously moved, false otherwise
     */
    @Override
    public boolean execute(Player player, GameModel model, GameView view) {
        displayUpgradeOptions(player, view);
        int chosenRank = getUserChosenRank(view);
        if (!validateChosenRank(chosenRank, player.getRank(), view)) {
            return false;
        }
        String paymentMethod = determinePaymentMethod(player, chosenRank, view);
        if (!isValidPaymentMethod(paymentMethod, view)) {
            return false;
        }
        processPayment(player, chosenRank, paymentMethod);
        upgradePlayerRank(player, chosenRank, view);
        // End the turn if the player has already moved
        return player.getHasMoved();
    }

    /**
     * Displays the upgrade options for the player.
     * 
     * @param player The player
     * @param view The game view
     * @return true if the player can upgrade, false otherwise
     */
    private void displayUpgradeOptions(Player player, GameView view) {
        view.showMessage("You can upgrade to one of the following ranks:");
        for (int i = player.getRank() + 1; i <= 6; i++) {
            String message = String.format("%d - $%4d or %4d credits", i, 
                                    dollarCosts.getOrDefault(i, 0), 
                                    creditCosts.getOrDefault(i, 0));
            view.showMessage(message);
        }
    }

    /**
     * Gets the rank chosen by the player
     * @param view the game view
     * @return The rank chosen by the player
     */
    private int getUserChosenRank(GameView view) {
        String input = view.getPlayerInput();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            // Handle invalid input
            return -1; // or any other default value
        }
    }

    /**
     * Validates the chosen rank
     *
     * @param chosenRank The rank chosen by the player
     * @param playerRank The rank of the player
     * @param view The game view
     * @return true if the chosen rank is valid, false otherwise
     */
    private boolean validateChosenRank(int chosenRank, int playerRank, GameView view) {
        if (chosenRank <= playerRank || chosenRank > 6) {
            view.showMessage("Invalid rank. Rank must be between " + (playerRank + 1) + " and 6 inclusively.");
            return false;
        }
        return true;
    }

    /**
     * Determines the payment method
     * 
     * @param player The player
     * @param chosenRank The rank chosen by the player
     * @param view The game view
     * @return The payment method
     */
    private String determinePaymentMethod(Player player, int chosenRank, GameView view) {
        boolean canAffordWithDollars = player.getMoney() >= dollarCosts.getOrDefault(chosenRank, 0);
        boolean canAffordWithCredits = player.getCredits() >= creditCosts.getOrDefault(chosenRank, 0);
        if (canAffordWithDollars && canAffordWithCredits) {
            view.showMessage("You can afford to upgrade to this rank with either money or credits.");
            view.showMessage("Would you like to pay with money or credits?");
            return view.getPlayerInput();
        } else if (canAffordWithDollars) {
            return "money";
        } else if (canAffordWithCredits) {
            return "credits";
        }
        return null;
    }

    /**
     * Validates payment method
     * 
     * @param paymentMethod The payment method
     * @param view The game view
     * @return true if the payment method is valid, false otherwise
     */
    private boolean isValidPaymentMethod(String paymentMethod, GameView view) {
        if (paymentMethod == null) {
            view.showMessage("You cannot afford to upgrade to this rank.");
            return false;
        }
        if (!"money".equals(paymentMethod) && !"credits".equals(paymentMethod)) {
            view.showMessage("Invalid payment method.");
            return false;
        }
        return true;
    }

    /**
     * Processes payment
     * 
     * @param player The player
     * @param chosenRank The rank chosen by the player
     * @param paymentMethod The payment method
     * @return true if the payment was successful, false otherwise
     */
    private void processPayment(Player player, int chosenRank, String paymentMethod) {
        if ("money".equals(paymentMethod)) {
            player.setMoney(player.getMoney() - dollarCosts.get(chosenRank));
        } else if ("credits".equals(paymentMethod)) {
            player.setCredits(player.getCredits() - creditCosts.get(chosenRank));
        }
    }

    /**
     * Upgrades player rank
     * 
     * @param player The player
     * @param chosenRank The rank chosen by the player
     * @param view The game view
     * @return true if the player was successfully upgraded, false otherwise
     */
    private void upgradePlayerRank(Player player, int chosenRank, GameView view) {
        player.setRank(chosenRank);
        view.showMessage("You have successfully upgraded to rank " + chosenRank + ".");
        player.setHasUpgraded(true);
    }
}
