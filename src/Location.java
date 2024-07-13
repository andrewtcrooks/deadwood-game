import java.util.*;

/**
 * Represents a Location in the game.
 */
public class Location {
    private String name;
    private List<String> neighbors;
    private Area area;
    private List<Take> takes;
    private boolean wrapped;
    private List<Role> locationRoles;
    // private List<Role> allRoles;
    // private SceneCard scene;

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
        this.neighbors = neighbors;
        this.area = area;
        this.takes = takes;
        this.wrapped = false;
        this.locationRoles = roles;
        // this.allRoles = roles;
        // this.scene = null;
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

    // /**
    //  * Returns the all roles for the Location and Scene.
    //  *
    //  * @return the all roles for the Location and Scene
    //  */
    // public List<Role> getRoles() {
    //     return allRoles;
    // }

    // /**
    //  * Returns the scene card for the Location.
    //  *
    //  * @return the scene card for the Location
    //  */
    // public SceneCard getSceneCard() {
    //     return scene;
    // }

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
     * Wraps the Location.
     */
    public void setSceneWrapped() {
        this.wrapped = true;
    }

    // /**
    //  * Sets a new scene card for the Location and redefines the roles list.
    //  *
    //  * @param scene the new scene card for the Location
    //  */
    // public void setSceneCard(SceneCard scene) {
    //     this.scene = scene;
    //     this.allRoles = new ArrayList<Role>(this.locationRoles); // Copy the roles from the location
    //     this.allRoles.addAll(scene.getRoles()); // Add all roles from the new scene
    //     this.wrapped = false;
    // }

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
     * Resets all takes in the Location.
     */
    public void resetTakes() {
        for (Take take : this.takes) {
            take.reset();
        }
    }

    // /**
    //  * Clears the scene card from the Location and redefines the roles list.
    //  */
    // public void clearSceneCard() {
    //     this.scene = null;
    //     this.allRoles = new ArrayList<Role>(this.locationRoles);
    // }

}
