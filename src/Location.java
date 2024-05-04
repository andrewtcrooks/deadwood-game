import java.util.*;

/**
 * Represents a location in the game.
 */
public class Location {
    private String name;
    private int shots;
    private List<String> neighbors;
    private List<Role> roles;
    private SceneCard scene;

    /**
     * Constructs a new Location with the given parameters.
     *
     * @param name the name of the location
     * @param shots the number of shots in the location
     * @param neighbors the neighboring locations
     * @param roles the roles for the location
     * @param scene the scene card for the location
     */
    Location(String name, int shots, List<String> neighbors, List<Role> roles, SceneCard scene) {
        this.name = name;
        this.shots = shots;
        this.neighbors = neighbors;
        this.roles = new ArrayList<>(roles); // Create a new list to avoid modifying the original list
        this.roles.addAll(scene.getRoles()); // Add all roles from the scene
        this.scene = scene;
    }

    /**
     * Returns the name of the location.
     *
     * @return the name of the location
     */
    String getName() {
        return name;
    }

    /**
     * Returns the number of shots in the location.
     *
     * @return the number of shots in the location
     */
    int getShots() {
        return shots;
    }

    /**
     * Returns the neighboring locations.
     *
     * @return the neighboring locations
     */
    List<String> getNeighbors() {
        return neighbors;
    }

    /**
     * Returns the roles for the location.
     *
     * @return the roles for the location
     */
    List<Role> getRoles() {
        return roles;
    }

    /**
     * Returns the scene card for the location.
     *
     * @return the scene card for the location
     */
    SceneCard getScene() {
        return scene;
    }
}
