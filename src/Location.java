import java.util.*;

/**
 * Represents a Location in the game.
 */
public class Location {
    private String name;
    private List<String> neighbors;
    private Area area;
    private int locationShots;
    private int shots;
    private List<Role> locationRoles;
    private List<Role> roles;
    private SceneCard scene;

    /**
     * Constructs a new Location with the given parameters.
     *
     * @param name the name of the Location
     * @param neighbors the neighbors of the Location
     * @param area the area of the Location
     * @param shots the number of shots in the Location
     * @param roles the roles for the Location
     * @param scene the scene card for the Location
     */
    Location(String name, List<String> neighbors, Area area, int shots, List<Role> roles) {
        this.name = name;
        this.neighbors = neighbors;
        this.area = area;
        this.locationShots = shots; // Store initial shots for reset
        this.shots = shots;
        this.locationRoles = new ArrayList<>(roles); // Store location roles
        this.roles = new ArrayList<>(roles); // Create a new list to avoid modifying the locationRoles list
        this.roles.addAll(scene.getRoles()); // Add all roles from the scene
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
     * Returns the number of shots left in the Location.
     *
     * @return the number of shots left in the Location
     */
    int getShots() {
        return shots;
    }

    /**
     * Returns the roles for the Location.
     *
     * @return the roles for the Location
     */
    List<Role> getRoles() {
        //make a copy of the location's roles
        List<Role> roles = new ArrayList<>(locationRoles);
        // Get the roles from the scene card
        List<Role> sceneRoles = scene.getRoles();
        roles.addAll(sceneRoles);

        return roles;
    }

    /**
     * Returns the scene card for the Location.
     *
     * @return the scene card for the Location
     */
    SceneCard getScene() {
        return scene;
    }

    /**
     * Decrements the number of shots in the Location.
     */
    void decrementShots() {
        this.shots -= shots;
    }

    /**
     * Resets the number of shots in the Location.
     */
    void resetShots() {
        this.shots = locationShots;
    }

    /**
     * Sets a new scene card for the Location and redefines the roles list.
     *
     * @param scene the new scene card for the Location
     */
    void setScene(SceneCard scene) {
        this.scene = scene;
        this.roles = new ArrayList<>(locationRoles); // Reset roles to Location roles
        this.roles.addAll(scene.getRoles()); // Add all roles from the new scene
    }
}
