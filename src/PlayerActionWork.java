import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/*
 * Represents the work action for the player.
 */
public class PlayerActionWork implements PlayerAction {

    /**
     * Validates the work action for the player.
     *
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return true if the player can work, false otherwise
     */
    @Override
    public boolean validate(Player player, GameModel model, GameView view) {
        Board board = model.getBoard();
        return isNotInCastingOffice(player, board, view) && // true if not in Casting Office
               isNotInTrailer(player, board, view) && // true if not in Trailer
               isAtUnwrappedLocation(player, board, view) && // true if location is not wrapped
               playerHasNoRole(player, board, view); // true if player has no role
    }
    
    /**
     * Checks if the player is not in the Casting Office.
     *
     * @param player the player
     * @param view the game view
     * @return true if the player is not in the Casting Office, false otherwise
     */
    private boolean isNotInCastingOffice(Player player, Board board, GameView view) {
        if (board.getPlayerLocationName(player).equals("Casting Office")) {
            view.showMessage("There is no work in the Casting Office.");
            return false;
        }
        return true;
    }

    /**
     * Checks if the player is not in the Trailer.
     *
     * @param player the player
     * @param view the game view
     * @return true if the player is not in the Trailer, false otherwise
     */
    private boolean isNotInTrailer(Player player, Board board, GameView view) {
        if (board.getPlayerLocationName(player).equals("Trailer")) {
            view.showMessage("There is no work in your Trailer.");
            return false;
        }
        return true;
    }

    /**
     * Checks if the player is at an unwrapped location.
     *
     * @param player the player
     * @param view the game view
     * @return true if the location is not wrapped, false otherwise
     */
    private boolean isAtUnwrappedLocation(Player player, Board board, GameView view) {
        if (board.getPlayerLocation(player).getIsWrapped()) {
            view.showMessage("Location is wrapped. No more work available until tomorrow.");
            return false;
        }
        return true;
    }

    /**
     * Checks if the player has a role.
     *
     * @param player the player
     * @param view the game view
     * @return true if the player has no role, false otherwise
     */
    private boolean playerHasNoRole(Player player, Board board, GameView view) {
        Role role = board.getPlayerRole(player);
        if (role != null) {
            String roleName = role.getName();
            String roleLine = role.getLine();
            boolean onCard = role.getOnCard();
            String message = "Current role: " + roleName + " - \"" + roleLine + "\"";
            if (onCard) {
                view.showMessage(message);
            } else {
                view.showMessage(message + " (for scale)");
            }
            return false;
        }
        return true;
    }

    /**
     * Executes the work action for the player.
     *
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return true to end turn if player has previously moved, false otherwise
     */
    @Override
    public boolean execute(Player player, GameModel model, GameView view) {
        // Get the board
        Board board = model.getBoard();
        // Get the location name
        String locationName = board.getPlayerLocationName(player);
        // Get the available roles at the location
        List<Role> allRoles = board.getLocationRoles(locationName);
        // Display the available roles
        displayAvailableRoles(allRoles, view);
        // Get the selected role
        Role selectedRole = getPlayerSelectedRole(allRoles, view);
        if (selectedRole == null) {
            view.showMessage("Invalid role.");
            return false;
        }
        assignRoleToPlayer(player, selectedRole, board, view);
        return player.getHasMoved();
    }

    /**
     * Lists the available roles for the player.
     *
     * @param player the player
     * @param board the game board
     * @return the available roles for the player
     */
    private List<Role> listAvailableRoles(Player player, Board board) {
        String locationName = board.getPlayerLocationName(player);
        return board.getLocationRoles(locationName).stream()
                .filter(role -> role.getRank() <= player.getRank() && !role.isOccupied())
                .collect(Collectors.toList());
    }

    /**
     * Displays the available roles for the player.
     *
     * @param roles the available roles for the player
     * @param view the game view
     */
    private void displayAvailableRoles(List<Role> roles, GameView view) {
        view.showMessage("You can work the following roles:");
        roles.sort(Comparator.comparingInt(Role::getRank)
                .thenComparing(Role::getOnCard));
        roles.forEach(role -> {
            String message = role.getName() + (role.getOnCard() ? "" : " (for scale)") + " -> Rank " + role.getRank();
            view.showMessage(message);
        });
    }

    /**
     * Gets the role selected by the player.
     *
     * @param roles the available roles for the player
     * @param view the game view
     * @return the role selected by the player
     */
    private Role getPlayerSelectedRole(List<Role> roles, GameView view) {
        String roleString = view.getPlayerInput();
        return roles.stream()
                .filter(r -> r.getName().equals(roleString))
                .findFirst()
                .orElse(null);
    }

    /**
     * Assigns the role to the player.
     *
     * @param player the player
     * @param role the role
     * @param board the game board
     * @param view the game view
     */
    private void assignRoleToPlayer(Player player, Role role, Board board, GameView view) {
        board.setPlayerRole(player, role);
        view.showMessage("You are now working the role of " + role.getName());
        player.setHasWorked(true);
    }

}
