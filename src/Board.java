import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents the game board for the Deadwood game.
 */
public class Board {
    private int numScenesRemaining;
    private Deck deck;
    private Map<String, Location> locations;
    private Map<Player, String> playerLocation;
    private Map<Player, Role> playerRole;
    private Map<String, SceneCard> locationScene;
    private Map<String, List<Role>> locationRoles;
    // private Map<SceneCard, List<Role>> sceneRoles;


/************************************************************
 * Contructor and initialization methods
 ************************************************************/

    /**
     * Constructs a new Board with the given deck and locations.
     *
     * @param deck The deck of cards
     * @param locations The map of locations
     */
    public Board(Deck deck, Map<String, Location> locations) {
        this.deck = deck;
        this.locations = locations;
        // Initialize number of scenes remaining
        this.numScenesRemaining = 10;
        // Initialize players at locations map
        this.playerLocation = new HashMap<>();
        // Initialize player roles map
        this.playerRole = new HashMap<>();
        // Initialize location scene map
        this.locationScene = new HashMap<>();
        // Initialize location roles map
        this.locationRoles = new HashMap<>();        
        // Deal scene cards to locations
        dealSceneCardsToLocations();
    }

    /**
     * Deals new scene card to each location on the board.
     */
    private void dealSceneCardsToLocations() {
        for (Location location : this.locations.values()) {
            if (location.getName().equals("Trailer") || location.getName().equals("Casting Office")) {
                continue; // Skip the current iteration for Trailer and Casting Office locations
            }
            // Draw a scene card from the deck
            SceneCard card = this.deck.drawCard();
            // Set the scene card for the location
            this.locationScene.put(location.getName(), card);
            // Set the roles for the location
            this.locationRoles.put(location.getName(), card.getRoles());
        }
    }


/************************************************************
 * Game State Management
 ************************************************************/

    /**
     * Resets the board to its initial state.
     */
    public void resetBoard() {
        dealSceneCardsToLocations();
    }

    /**
     * Returns the number of scenes remaining in the game.
     *
     * @return The number of scenes remaining in the game.
     */
    public int getNumScenesRemaining() {
        return this.numScenesRemaining;
    }

    /**
     * Resets the number of scenes remaining in the game.
     */
    public void resetNumScenesRemaining() {
        this.numScenesRemaining = 10;
    }

    /**
     * Decrements the number of scenes remaining in the game by 1.
     *
     * @return The new number of scenes remaining in the game.
     */
    private int decrementNumScenesRemaining() {
        return --this.numScenesRemaining;
    }


/************************************************************
 * Scene and Location Management
 ************************************************************/

    /**
     * Wraps the scene in the Location.
     */
    public void wrapScene(Location location) {
        // Check for any player working a role on a card
        List<Player> playersAtLocation = getLocationPlayers(location);
        boolean anyPlayerOnCard = playersAtLocation.stream()
            .anyMatch(player -> getPlayerRole(player) != null && getPlayerRole(player).getOnCard());
        // Pay out bonus if any player was on a card
        if (anyPlayerOnCard) {
            payOutBonus(location);
        }
        // Remove all players from their roles and reset rehearsal tokens
        playersAtLocation.forEach(player -> {
            setPlayerRole(player, null);
            player.resetRehearsalTokens();
        });
        // Reset takes
        location.resetTakes();
        // Clear the scene card        
        setLocationSceneCard(location.getName(), null);
        // set wrapped to true
        location.setSceneWrapped();
        // decrement the number of scenes remaining
        decrementNumScenesRemaining();
    }

    /**
     * Clears the scene card from the Location and redefines the roles list.
     */
    public void clearSceneCard(String locationName) {
        // Overwrite the scene card with null
       this.locationScene.put(locationName, null);
        // // 
        // Location location = locations.get(locationName);
        // List<Role> locationRoles = location.getRoles();
        // // Set the roles for the location to just the 
        // this.locationRoles.put(locationName, locationRoles);

        // TODO: definitely somehting to fix here



        // this.allRoles = new ArrayList<Role>(this.locationRoles);
    }

    /**
     * Wraps a shot at a location.
     * 
     * @param location The location to wrap a shot at.
     */
    public void wrapLocationShot(Location location) {
        location.wrapShot();
    }

        /**
     * Returns the locations on the board.
     * 
     * @return The locations on the board.
     */
    public Map<String, Location> getLocations() {
        return this.locations;
    }

    /**
     * Returns the number of shots remaining at a location.
     *  
     * @param location The location to check.
     * @return The number of shots remaining at the location.
     */
    public int getLocationShotsRemaining(Location location) {
        return location.getShots();
    }

    /**
     * Returns the neighbors of a location.
     *
     * @param location The location to check.
     * @return The neighbors of the location.
     */
    public List<String> getLocationNeighbors(Location location) {
        return location.getNeighbors();
    }


/************************************************************
 * Player Location and Movement Management
 ************************************************************/

    /**
     * Sets a player's location on the board.
     * 
     * @param player The player to set the location for.
     * @param locationName The name of the location to set.
     */
    public void setPlayerLocation(Player player, String locationName) {
        playerLocation.put(player, locationName);
    }

    /**
     * Returns the name of the location a player is at.
     * 
     * @param player The player to check.
     * @return The name of the location the player is at.
     */
    public String getPlayerLocationName(Player player) {
        return playerLocation.get(player);
    }

    /**
     * Returns the location a player is at.
     * 
     * @param player The player to check.
     * @return The location the player is at.
     */
    public Location getPlayerLocation(Player player) {
        String locationName = playerLocation.get(player);
        return locations.get(locationName);
    }
    
    /**
     * Returns the players at a location.
     * 
     * @param location The location to check.
     * @return The players at the location.
     */
    public List<Player> getLocationPlayers(Location location) {
        List<Player> players = new ArrayList<>();
        for (Player player : playerLocation.keySet()) {
            if (playerLocation.get(player).equals(location.getName())) {
                players.add(player);
            }
        }
        return players;
    }


/************************************************************
 * Role and Scene Management
 ************************************************************/

    /**
     * Sets a player's role.
     *
     * @param player The player to set the role for.
     * @param role The role to set.
     */
    public void setPlayerRole(Player player, Role role) {
        playerRole.put(player, role);
    }

    /**
     * Returns a player's role.
     *
     * @param player The player to check.
     * @return The player's role.
     */
    public Role getPlayerRole(Player player) {
        return playerRole.get(player);
    }

    /**
     * Sets the scene card at a location.
     * 
     * @param locationName The name of the location to set the scene card for.
     * @param sceneCard The scene card to set.
     */
    public void setLocationSceneCard(String locationName, SceneCard sceneCard) {
        locationScene.put(locationName, sceneCard);
    }

    /**
     * Returns the scene card at a location.
     * 
     * @param locationName The name of the location to check.
     * @return The scene card at the location.
     */
    public SceneCard getLocationSceneCard(String locationName) {
        return locationScene.get(locationName);
    }

    /**
     * Returns the roles for the scene card at a location.
     * 
     * @param location The location to check.
     * @return The roles for the scene card at the location.
     */
    public SceneCard getLocationSceneCardRoles(String locationName) {
        return locationScene.get(locationName);
    }

    /**
     * Returns the roles at a location.
     *
     * @param locationName The name of the location to check.
     * @return The roles at the location.
     */
    public List<Role> getLocationRoles(String locationName) {
        List<Role> allRoles = new ArrayList<Role>(locationRoles.get(locationName));
        allRoles.addAll(locationScene.get(locationName).getRoles());
        return allRoles.stream()
            .sorted(Comparator.comparingInt(Role::getRank)
                    .thenComparing(Role::getOnCard, Comparator.reverseOrder()))
            .collect(Collectors.toList());
    }


/************************************************************
 * Bonus and Payout Management
 ************************************************************/

    /**
     * Pays out the bonus to all players at the Location.
     */
    private void payOutBonus(Location location) {
        int movieBudget = getLocationSceneCard(location.getName()).getBudget(); // Get the movie budget
        List<Integer> diceRolls = rollDice(movieBudget); // Roll dice equal to the budget
        Collections.sort(diceRolls, Collections.reverseOrder()); // Sort dice rolls in descending order
        // Get all players with an on card role, sorted by their role rank in descending order
        List<Player> playersOnCard = getLocationPlayers(location).stream()
            .filter(player -> getPlayerRole(player) != null &&  getPlayerRole(player).getOnCard())
            .sorted(Comparator.comparingInt(Player::getRoleRank).reversed())
            .collect(Collectors.toList());
        // Distribute dice rolls as money in a round-robin fashion
        for (int i = 0; i < diceRolls.size(); i++) {
            Player player = playersOnCard.get(i % playersOnCard.size()); // Wrap around if more dice than players
            player.addMoney(diceRolls.get(i)); // Add the dice roll as a bonus
        }
        // Pay each player with an off card role an amount equal to the rank of the role they are working
        List<Player> playersOffCard = getLocationPlayers(location).stream()
            .filter(player -> getPlayerRole(player) != null && !getPlayerRole(player).getOnCard())
            .collect(Collectors.toList());
        playersOffCard.forEach(player -> player.addMoney(player.getRoleRank()));
        // Players leave their roles
        playersOnCard.forEach(player -> setPlayerRole(player, null));
        playersOffCard.forEach(player -> setPlayerRole(player, null));
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