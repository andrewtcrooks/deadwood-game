import java.util.*;

/**
 * Represents a location in the game.
 */
public class Location {
    private String name;
    private int locationShots;
    private int shots;
    private List<String> neighbors;
    private List<Role> locationRoles;
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
        this.locationShots = shots; // Store initial shots for reset
        this.shots = shots;
        this.neighbors = neighbors;
        this.locationRoles = new ArrayList<>(roles); // Store location roles
        this.roles = new ArrayList<>(roles); // Create a new list to avoid modifying the locationRoles list
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
     * Returns the number of shots left in the location.
     *
     * @return the number of shots left in the location
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

    /**
     * Decrements the number of shots in the location.
     */
    void decrementShots() {
        this.shots -= shots;
    }

    /**
     * Resets the number of shots in the location.
     */
    void resetShots() {
        this.shots = locationShots;
    }

    /**
     * Sets a new scene card for the location and redefines the roles list.
     *
     * @param scene the new scene card for the location
     */
    void setScene(SceneCard scene) {
        this.scene = scene;
        this.roles = new ArrayList<>(locationRoles); // Reset roles to location roles
        this.roles.addAll(scene.getRoles()); // Add all roles from the new scene
    }
}
