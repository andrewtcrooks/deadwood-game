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
    Location(String name, List<String> neighbors, Area area, List<Take> takes, List<Role> roles) {
        this.name = name;
        this.players = null;
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
    String getName() {
        return name;
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
    List<String> getNeighbors() {
        return neighbors;
    }

    /**
     * Returns the area of the Location.
     *
     * @return the area of the Location
     */
    Area getArea() {
        return area;
    }

    /**
     * Returns the takes for the Location.
     *
     * @return the takes for the Location
     */
    List<Take> getTakes() {
        return takes;
    }
    
    void removeShotCounter() {
        Take smallestTake = null;
    
        // Find the Take with the smallest integer name that is not already wrapped
        for (Take take : this.takes) {
            if (!take.isWrapped() && (smallestTake == null || take.getNumber() < smallestTake.getNumber())) {
                smallestTake = take;
            }
        }
    
        // Wrap the Take with the smallest integer name
        if (smallestTake != null) {
            smallestTake.wrap();
        } else {
            this.wrapped = true;
        }
    }

    /**
     * Returns the number of shots left in the Location.
     *
     * @return the number of shots left in the Location
     */
    int getShots() {
        int shots = 0;
        for (Take take : this.takes) {
            if (!take.isWrapped()) {
                shots += 1;
            }
        }
        return shots;
    }

    /**
     * Returns the all roles for the Location and Scene.
     *
     * @return the all roles for the Location and Scene
     */
    List<Role> getRoles() {
        return allRoles;
    }

    /**
     * Returns the scene card for the Location.
     *
     * @return the scene card for the Location
     */
    SceneCard getSceneCard() {
        return scene;
    }

    /**
     * Wraps the next available shot in the Location.
     */
    void wrapShot() {
        // wrap take with the lowest number from getNumber(), the numbers can range from 1 up to 4
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

        // Check if all takes are wrapped
        boolean allWrapped = true;
        for (Take take : this.takes) {
            if (!take.isWrapped()) {
                allWrapped = false;
                break;
            }
        }

        // If all takes are wrapped, wrap the scene
        if (allWrapped) {
            wrapScene();
            
        }
    }

    public void wrapScene() {
        this.wrapped = true;

        // remove all players from their roles while checking for a single player onCard
        List<Player> playersAtLocation = getPlayers();
        boolean anyPlayerOnCard = false;
        for (Player player : playersAtLocation) {
            if (player.getOnCard()) {
                anyPlayerOnCard = true;
            }
            player.leaveRole();
        }

        // pay out bonus to all players at locatoin if any player was on a card
        if (anyPlayerOnCard) {
            payOutBonus(); // Call payOutBonus if any player was on a card
            
        }

        // clear the scene card after paying out bonus
        clearSceneCard();
    }

    /**
     * Pays out the bonus to all players at the Location.
     */
    void payOutBonus() {
        int movieBudget = getSceneCard().getBudget(); // Get the movie budget
        List<Integer> diceRolls = rollDice(movieBudget); // Roll dice equal to the budget
        Collections.sort(diceRolls, Collections.reverseOrder()); // Sort dice rolls in descending order

        // Get all players on the card, sorted by their role rank in descending order
        List<Player> playersOnCard = getPlayers().stream()
            .filter(Player::getOnCard)
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
        .filter(player -> player.getRole() != null && !player.getOnCard())
        .forEach(player -> player.addMoney(player.getRole().getRank()));
    }
    
    /**
     * Rolls a number of dice and returns the results.
     *
     * @param numDice the number of dice to roll
     * @return the results of the dice rolls
     */
    List<Integer> rollDice(int numDice) {
        List<Integer> rolls = new ArrayList<>();
        for (int i = 0; i < numDice; i++) {
            Dice dice = new Dice(); // Use the Dice class to roll the dice
            rolls.add(dice.getValue()); // Get the value of the roll
        }
        return rolls;
    }

    /**
     * Returns whether the Location is wrapped.
     *
     * @return whether the Location is wrapped
     */
    boolean isWrapped() {
        return wrapped;
    }

    /**
     * Sets a new scene card for the Location and redefines the roles list.
     *
     * @param scene the new scene card for the Location
     */
    void setSceneCard(SceneCard scene) {
        this.scene = scene;
        this.allRoles = new ArrayList<Role>(this.locationRoles); // Copy the roles from the location
        this.allRoles.addAll(scene.getRoles()); // Add all roles from the new scene
        this.wrapped = false;
    }

    /**
     * Clears the scene card from the Location and redefines the roles list.
     */
    public void clearSceneCard() {
        this.scene = null;
        this.allRoles = new ArrayList<Role>(this.locationRoles);
    }
}
