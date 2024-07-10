import java.util.*;

/**
 * Represents the game board for the Deadwood game.
 */
public class Board {
    private int numScenesRemaining;
    private Deck deck;
    private Map<String, Location> locations;
    private Map<Player, String> playerLocations;

    /**
     * Constructs a new Board with the given deck and locations.
     *
     * @param deck The deck of cards to use.
     * @param locations The locations on the board.
     */
    public Board(Deck deck, Map<String, Location> locations) {
        this.deck = deck;
        this.locations = locations;
        // Initialize number of scenes remaining
        this.numScenesRemaining = 10;
        // Initialize player locations map
        this.playerLocations = new HashMap<>();
        // Deal scene cards to locations
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
     * Decrements the number of scenes remaining in the game by 1.
     *
     * @return The new number of scenes remaining in the game.
     */
    public int decrementNumScenesRemaining() {
        return --this.numScenesRemaining;
    }

    /**
     * Resets the number of scenes remaining in the game.
     */
    public void resetNumScenesRemaining() {
        this.numScenesRemaining = 10;
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

    /**
     * Returns the roles at a location.
     *
     * @param location The location to check.
     * @return The roles at the location.
     */
    public List<Role> getLocationRoles(Location location) {
        return location.getRoles();
    }

    /**
     * Returns the scene card at a location.
     *
     * @param location The location to check.
     * @return The scene card at the location.
     */
    public SceneCard getLocationScene(Location location) {
        return location.getSceneCard();
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
     * Returns the players at a location.
     * 
     * @param location The location to check.
     * @return The players at the location.
     */
    public List<Player> getPlayersAtLocation(Location location) {
        List<Player> players = new ArrayList<>();
        for (Player player : playerLocations.keySet()) {
            if (playerLocations.get(player).equals(location.getName())) {
                players.add(player);
            }
        }
        return players;
    }

    /**
     * Resets the board to its initial state.
     */
    public void resetBoard() {
        dealSceneCardsToLocations();
    }

    /**
     * Deals new scene card to each location on the board.
     */
    private void dealSceneCardsToLocations() {
        for (Location location : this.locations.values()) {
            if (location.getName().equals("Trailer") || location.getName().equals("Casting Office")) {
                continue; // Skip the current iteration for these locations
            }
            SceneCard card = this.deck.drawCard();
            location.setSceneCard(card);
        }
    }

    /**
     * Sets a player's location on the board.
     * 
     * @param player The player to set the location for.
     * @param locationName The name of the location to set.
     */
    public void setPlayerLocation(Player player, String locationName) {
        playerLocations.put(player, locationName);
    }

    /**
     * Returns the name of the location a player is at.
     * 
     * @param player The player to check.
     * @return The name of the location the player is at.
     */
    public String getPlayerLocationName(Player player) {
        return playerLocations.get(player);
    }

    /**
     * Returns the location a player is at.
     * 
     * @param player The player to check.
     * @return The location the player is at.
     */
    public Location getPlayerLocation(Player player) {
        String locationName = playerLocations.get(player);
        return locations.get(locationName);
    }

}