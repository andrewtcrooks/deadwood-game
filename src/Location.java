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
        this.neighbors = neighbors;
        this.area = area;
        this.takes = takes;
        this.wrapped = false;
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

        // If all takes are wrapped, set this.wrapped to true
        if (allWrapped) {
            this.wrapped = true;
        }
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
        this.allRoles.addAll(scene.getRoles()); // Add all roles from the new scene
    }
}
