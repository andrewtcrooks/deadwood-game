
/**
 * Represents a role with a name, rank, line, and indicator if it is on a Scene Card or at a Location.
 */
public class Role {
    private String name;
    private int rank;
    private String line;
    private boolean onCard;
    private boolean occupied;
    private Player player;

    /**
     * Initializes a new Role with the given name, rank, line, and onCard indicator.
     *
     * @param name The name of the Role.
     * @param rank The rank of the Role.
     * @param line The line for the Role.
     * @param onCard True if the Role is from a SceneCard, false if Role is from a Location.
     */
    Role(String name, int rank, String line, boolean onCard) {
        this.name = name;
        this.rank = rank;
        this.line = line;
        this.onCard = onCard;
        this.occupied = false;
        this.player=null;
    }

    /**
     * Returns the name of the Role.
     *
     * @return The name of the Role.
     */
    String getName() { 
        return name; 
    }

    /**
     * Returns the rank of the Role.
     *
     * @return The rank of the Role.
     */
    int getRank() { 
        return rank; 
    }

    /**
     * Returns the line for the Role.
     *
     * @return The line for the Role.
     */
    String getLine() { 
        return line; 
    }

    /**
     * Returns true if the Role is from a SceneCard, false if Role is from a Location.
     *
     * @return true if the Role is from a SceneCard, false if Role is from a Location.
     */
    boolean onCard() { 
        return onCard; 
    }

    /**
     * Returns true if the Role is occupied, false otherwise.
     *
     * @return True if the Role is occupied, false otherwise.
     */
    boolean isOccupied() {
        return occupied;
    }

    /**
     * Sets the occupied attribute of the role.
     * 
     * @param occupied The new value of the occupied attribute.
     */
    private void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    /** 
     * Assigns a player to the role.
     *
     * @param player The player to assign to the role
     */
    void assignPlayer(Player player) {
        this.player = player;
        setOccupied(true);
    }

    /**
     * Removes the player from the role.
     */
    void removePlayer() {
        this.player = null;
        setOccupied(false);
    }

    /**
     * Returns the player assigned to the Role.
     *
     * @return The player assigned to the Role.
     */
    Player getPlayer() {
        return player;
    }
}