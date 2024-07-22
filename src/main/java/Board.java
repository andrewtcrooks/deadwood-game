import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents the game board for the Deadwood game.
 */
public class Board {
    private int numScenesRemaining;
    // maps player ID to location name
    private Map<Integer, String> playerLocation;
    // maps player ID to role name
    private Map<Integer, String> playerRole;
    // maps location name to scene card ID
    private Map<String, Integer> locationScene;


/************************************************************
 * Contructor and Initialization
 ************************************************************/

    /**
     * Constructs a new Board with the given deck and locations.
     *
     * @param deck The deck of cards
     * @param locations The map of locations
     */
    public Board(Deck deck, Map<String, Location> locations) {
        // Initialize number of scenes remaining
        this.numScenesRemaining = 10;
        // Initialize players at locations map
        this.playerLocation = new HashMap<>();
        // Initialize player roles map
        this.playerRole = new HashMap<>();
        // Initialize location scene map
        this.locationScene = new HashMap<>();
        // Deal scene cards to locations
        dealSceneCardsToLocations(deck, locations);
    }

    /**
     * Deals new scene card to each location on the board.
     */
    private void dealSceneCardsToLocations(Deck deck, Map<String,Location> locations) {
        for (Location location : locations.values()) {
            if (location.getName().equals("Trailer") || location.getName().equals("Casting Office")) {
                continue; // Skip the current iteration for Trailer and Casting Office locations
            }
            // Draw a scene card from the deck
            SceneCard card = deck.drawCard();
            // Get the scene card ID
            int sceneCardID = card.getID();
            // Set the scene card for the location
            this.locationScene.put(location.getName(), sceneCardID);
        }
    }


/************************************************************
 * Game State Management
 ************************************************************/

    /**
     * Resets the board to its initial state.
     */
    public void resetBoard(Deck deck, Map<String,Location> locations) {
        // Unwrap all locations
        for (Location location : locations.values()) {
            location.setUnwrapped();
        }
        dealSceneCardsToLocations(deck, locations);
        resetNumScenesRemaining();
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
    private void resetNumScenesRemaining() {
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
    public void wrapScene(Player activePlayer, List<Player> players, Deck deck, Location location) {
        // Get players at location
        List<Player> playersAtLocation = getLocationPlayers(players, location);
        // Get the scene card at the location
        int sceneCardID = getLocationSceneCardID(location.getName());
        SceneCard sceneCard = deck.getDrawnCard(sceneCardID);
        // Get a list of the names of the on card roles
        List<String> roleNamesOnCard = sceneCard.getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toList());
        // Get the players working on card
        List<Player> playersOnCard = playersAtLocation.stream()
            .filter(player -> roleNamesOnCard.contains(getPlayerRole(player.getID())))
            .collect(Collectors.toList());
        // Get a list of the names of the off card roles
        List<String> rolesNamesOffCard = location.getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toList());
        // Get the players working off card
        List<Player> playersOffCard = playersAtLocation.stream()
            .filter(player -> rolesNamesOffCard.contains(getPlayerRole(player.getID())))
            .collect(Collectors.toList());
        // Pay out bonus if any player was on a card
        if (playersOnCard.size() > 0) {
            payOutBonus(activePlayer, playersOnCard, playersOffCard, deck, location);
        }
        // Remove all players from their roles and reset rehearsal tokens
        playersAtLocation.forEach(player -> setPlayerRole(player.getID(), null));
        playersAtLocation.forEach(player -> player.resetRehearsalTokens());
        // Reset takes
        location.resetTakes();
        // Clear the scene card from the location
        setLocationSceneCard(location.getName(), null);
        // set the scene card to discarded
        deck.discardCard(sceneCardID);
        // set wrapped to true
        location.setWrapped();
        // decrement the number of scenes remaining
        decrementNumScenesRemaining();
    }

    /**
     * Clears the scene card from the Location and redefines the roles list.
     */
    public void clearSceneCard(String locationName) {
        // Overwrite the scene card with null
       this.locationScene.put(locationName, null);
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
        playerLocation.put(player.getID(), locationName);
    }

    /**
     * Returns the name of the location a player is at.
     * 
     * @param player The player to check.
     * @return The name of the location the player is at.
     */
    public String getPlayerLocationName(Player player) {
        return playerLocation.get(player.getID());
    }

    /**
     * Returns the players at a location.
     * 
     * @param location The location to check.
     * @return The players at the location.
     */
    public List<Player> getLocationPlayers(List<Player> players, Location location) {
        // Create a list to store the players at the location
        List<Player> playersAtLocation = new ArrayList<>();
        // Iterate over the playerLocation map to find players at the location
        for (Map.Entry<Integer, String> entry : playerLocation.entrySet()) {
            if (entry.getValue().equals(location.getName())) {
                // Assuming getID returns the player's ID as an Integer
                Integer playerId = entry.getKey();
                // Assuming getPlayerById(int id) is a method that returns a Player object given a player's ID
                Player player = players.stream()
                    .filter(p -> Integer.valueOf(p.getID()).equals(playerId))
                    .findFirst()
                    .orElse(null);
                if (player != null) {
                    playersAtLocation.add(player);
                }
            }
        }
        return playersAtLocation;
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
    public void setPlayerRole(int playerID, String roleName) {
        playerRole.put(playerID, roleName);
    }

    /**
     * Returns a player's role.
     *
     * @param player The player to check.
     * @return The player's role.
     */
    public String getPlayerRole(int playerID) {
        return playerRole.get(playerID);
    }

    /**
     * Sets the scene card at a location.
     * 
     * @param locationName The name of the location to set the scene card for.
     * @param sceneCard The scene card to set.
     */
    public void setLocationSceneCard(String locationName, Integer sceneCardID) {
        locationScene.put(locationName, sceneCardID);
    }

    /**
     * Returns the scene card at a location.
     * 
     * @param locationName The name of the location to check.
     * @return The scene card at the location.
     */
    public Integer getLocationSceneCardID(String locationName) {
        return locationScene.get(locationName);
    }

    /**
     * Returns the roles for the scene card at a location.
     * 
     * @param location The location to check.
     * @return The roles for the scene card at the location.
     */
    public List<Role> getLocationSceneCardRoles(String locationName, Deck deck) {
        // Get the scene card at the location
        int sceneCardID = getLocationSceneCardID(locationName);
        SceneCard sceneCard = deck.getDrawnCard(sceneCardID);
        // Get the roles for the scene card
        return sceneCard.getRoles();
    }


/************************************************************
 * Bonus and Payout Management
 ************************************************************/

    /**
     * Pays out the bonus to all players at the Location.
     */
    private void payOutBonus(Player activePlayer, List<Player> playersOnCard, List<Player> playersOffCard, Deck deck, Location location) {
        // Get the scene card at the location
        int sceneCardID = getLocationSceneCardID(location.getName());
        SceneCard sceneCard = deck.getDrawnCard(sceneCardID);
        // Get the movie budget
        int movieBudget = sceneCard.getBudget();
        // Roll a number of dice equal to the budget
        List<Integer> diceRolls = rollDice(movieBudget);
        // Sort dice rolls in descending order
        Collections.sort(diceRolls, Collections.reverseOrder());
        // Distribute dice rolls as money in a round-robin fashion, starting from the active player
        int numPlayersOnCard = playersOnCard.size();
        int numDice = diceRolls.size();
        // Pay out the on card players an amount equal to the dice roll, distributed in a round-robin fashion
        Player playerToPay = activePlayer;
        for (int i = 0; i < numDice; i++) {
            playerToPay.addMoney(diceRolls.get(i));
            playerToPay = playersOnCard.get((playersOnCard.indexOf(playerToPay) + 1) % numPlayersOnCard);
        }
        // Pay out the off card players an amount equal to the rank of their role
        List<Role> roles = location.getRoles();
        for (Player player : playersOffCard) {
            Role role = roles.stream()
                .filter(r -> r.getName().equals(getPlayerRole(player.getID())))
                .findFirst()
                .orElse(null);
            player.addMoney(role.getRank());
        }
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