/**
 * Represents a Role with a name, rank, line, and indicator if it is on a Scene Card or at a Location.
 */
public class Role {
    private String name;
    private int rank;
    private Area area;
    private String line;
    private boolean onCard;
    private boolean occupied;

    /**
     * Initializes a new Role with the given name, rank, line, and onCard indicator.
     *
     * @param name The name of the Role.
     * @param rank The rank of the Role.
     * @param area The area of the Role.
     * @param line The line for the Role.
     * @param onCard True if the Role is from a SceneCard, false if Role is from a Location.
     */
    Role(String name, int rank, Area area, String line, boolean onCard) {
        this.name = name;
        this.rank = rank;
        this.area = area;
        this.line = line;
        this.onCard = onCard;
        this.occupied = false;
    }

    /**
     * Returns the name of the Role.
     *
     * @return The name of the Role.
     */
    public String getName() { 
        return this.name; 
    }

    /**
     * Returns the rank of the Role.
     *
     * @return The rank of the Role.
     */
    public int getRank() { 
        return this.rank; 
    }

    /**
     * Returns the area of the Role.
     *
     * @return The area of the Role.
     */
    public Area getArea() {
        return this.area;
    }

    /**
     * Returns the line for the Role.
     *
     * @return The line for the Role.
     */
    public String getLine() { 
        return this.line; 
    }

    /**
     * Returns true if the Role is from a SceneCard, false if Role is from a Location.
     *
     * @return true if the Role is from a SceneCard, false if Role is from a Location.
     */
    public boolean getOnCard() { 
        return this.onCard; 
    }

    /**
     * Returns true if the Role is occupied, false otherwise.
     *
     * @return True if the Role is occupied, false otherwise.
     */
    public boolean isOccupied() {
        return this.occupied;
    }

    /**
     * Sets the occupied attribute of the Role.
     * 
     * @param occupied The new value of the occupied attribute.
     */
    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

}