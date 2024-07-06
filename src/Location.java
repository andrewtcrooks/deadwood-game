import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a Location in the game.
 */
public class Location {
    private String name;
    private List<Player> players;
    private List<String> neighbors;
    private Area area;
    private List<Take> takes;
    private boolean wrapped;
    private List<Role> locationRoles;
    private List<Role> allRoles;
    private SceneCard scene;

    /**
     * Constructs a new Location with the given parameters.
     *
     * @param name the name of the Location
     * @param neighbors the neighbors of the Location
     * @param area the area of the Location
     * @param takes the takes for the Location
     * @param roles the roles for the Location
     */
    public Location(String name, List<String> neighbors, Area area, List<Take> takes, List<Role> roles) {
        this.name = name;
        this.players = new ArrayList<Player>();
        this.neighbors = neighbors;
        this.area = area;
        this.takes = takes;
        this.wrapped = false;
        this.locationRoles = roles;
        this.allRoles = roles;
        this.scene = null;
    }

    /**
     * Returns the name of the Location.
     *
     * @return the name of the Location
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the players at the Location.
     *
     * @return the players at the Location
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Returns the neighbors of the Location.
     *
     * @return the neighbors of the Location
     */
    public List<String> getNeighbors() {
        return neighbors;
    }

    /**
     * Returns the area of the Location.
     *
     * @return the area of the Location
     */
    public Area getArea() {
        return area;
    }

    /**
     * Returns the takes for the Location.
     *
     * @return the takes for the Location
     */
    public List<Take> getTakes() {
        return takes;
    }

    /**
     * Returns whether the Location is wrapped.
     *
     * @return whether the Location is wrapped
     */
    public boolean getIsWrapped() {
        return wrapped;
    }

    /**
     * Returns the all roles for the Location and Scene.
     *
     * @return the all roles for the Location and Scene
     */
    public List<Role> getRoles() {
        return allRoles;
    }

    /**
     * Returns the scene card for the Location.
     *
     * @return the scene card for the Location
     */
    public SceneCard getSceneCard() {
        return scene;
    }

    /**
     * Returns the number of shots left in the Location.
     *
     * @return the number of shots left in the Location
     */
    public int getShots() {
        int shots = 0;
        for (Take take : this.takes) {
            if (!take.isWrapped()) {
                shots += 1;
            }
        }
        return shots;
    }

    /**
     * Sets a new scene card for the Location and redefines the roles list.
     *
     * @param scene the new scene card for the Location
     */
    public void setSceneCard(SceneCard scene) {
        this.scene = scene;
        this.allRoles = new ArrayList<Role>(this.locationRoles); // Copy the roles from the location
        this.allRoles.addAll(scene.getRoles()); // Add all roles from the new scene
        this.wrapped = false;
    }

    /**
     * Adds a player to the Location.
     *
     * @param player the player to add
     */
    public void addPlayer(Player player) {
        players.add(player);
    }

    /**
     * Removes a player from the Location.
     *
     * @param player the player to remove
     */
    public void removePlayer(Player player) {
        players.remove(player);
    }

    /**
     * Removes a shot counter from the Location.
     */
    public void removeShotCounter() {
        Take smallestTake = null;
        // Find the Take with the smallest integer name that is not already wrapped
        for (Take take : this.takes) {
            if (!take.isWrapped() && (smallestTake == null || take.getNumber() < smallestTake.getNumber())) {
                smallestTake = take;
            }
        }
        // Wrap the Take with the smallest integer name
        // else mark the scene as wrapped
        if (smallestTake != null) {
            smallestTake.wrap();
        } else {
            this.wrapped = true;
        }
    }

    /**
     * Wraps the next available shot in the Location.
     */
    public void wrapShot() {
        // wrap take with the lowest number from getNumber(), 
        // the numbers can range from 1 up to 4
        int lowest = 4;
        Take lowestTake = null;
        for (Take take : this.takes) {
            if (take.getNumber() <= lowest && !take.isWrapped()) {
                lowest = take.getNumber();
                lowestTake = take;
            }
        }
        if (lowestTake != null) {
            lowestTake.wrap();
        }
    }

    /**
     * Wraps the scene in the Location.
     */
    public void wrapScene() {
        // Check for any player working a role on a card
        List<Player> playersAtLocation = getPlayers();
        boolean anyPlayerOnCard = playersAtLocation.stream()
            .anyMatch(player -> player.getRole() != null && player.getRole().getOnCard());
        // Pay out bonus if any player was on a card
        if (anyPlayerOnCard) {
            payOutBonus();
        }
        // Remove all players from their roles and reset rehearsal tokens
        playersAtLocation.forEach(player -> {
            player.leaveRole();
            player.resetRehearsalTokens();
        });
        // Reset takes
        resetTakes();
        // Clear the scene card
        clearSceneCard();
        // set wrapped to true
        this.wrapped = true;
    }

    /**
     * Pays out the bonus to all players at the Location.
     */
    private void payOutBonus() {
        int movieBudget = getSceneCard().getBudget(); // Get the movie budget
        List<Integer> diceRolls = rollDice(movieBudget); // Roll dice equal to the budget
        Collections.sort(diceRolls, Collections.reverseOrder()); // Sort dice rolls in descending order

        // Get all players on the card, sorted by their role rank in descending order
        List<Player> playersOnCard = getPlayers().stream()
            .filter(player -> player.getRole() != null && player.getRole().getOnCard())
            .sorted(Comparator.comparingInt(Player::getRoleRank).reversed())
            .collect(Collectors.toList());
        // Distribute dice rolls in a round-robin fashion
        for (int i = 0; i < diceRolls.size(); i++) {
            Player player = playersOnCard.get(i % playersOnCard.size()); // Wrap around if more dice than players
            player.addMoney(diceRolls.get(i)); // Add the dice roll as a bonus
        }
        // Players leave their roles after receiving bonuses
        playersOnCard.forEach(Player::leaveRole);
        // Pay each player who has a role but is not on the card an amount equal to the rank of the role they are in
        getPlayers().stream()
        .filter(player -> player.getRole() != null && !player.getRole().getOnCard())
        .forEach(player -> player.addMoney(player.getRole().getRank()));
    }
    
    /**
     * Resets all takes in the Location.
     */
    private void resetTakes() {
        for (Take take : this.takes) {
            take.reset();
        }
    }

    /**
     * Clears the scene card from the Location and redefines the roles list.
     */
    private void clearSceneCard() {
        this.scene = null;
        this.allRoles = new ArrayList<Role>(this.locationRoles);
    }

    /**
     * Rolls a number of dice and returns the results.
     *
     * @param numDice the number of dice to roll
     * @return the results of the dice rolls
     */
    private List<Integer> rollDice(int numDice) {
        List<Integer> rolls = new ArrayList<>();
        for (int i = 0; i < numDice; i++) {
            Dice dice = new Dice(); // Use the Dice class to roll the dice
            rolls.add(dice.getValue()); // Get the value of the roll
        }
        return rolls;
    }

}
