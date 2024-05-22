import java.util.*;

/**
 * Represents a scene card with an ID, title, description, image, budget, and roles.
 */
public class SceneCard {
    private int id;
    private String title;
    private String desc;
    private String image;
    private int budget;
    private List<Role> roles;

    /**
     * Initializes a new SceneCard with the given ID, title, description, image, budget, and roles.
     *
     * @param id The unique identifier of the SceneCard.
     * @param title The title of the SceneCard (i.e. "Evil Wears a Hat").
     * @param desc The description of the SceneCard.
     * @param image The image on the SceneCard. (i.e. "01.png")
     * @param budget The budget for the SceneCard.
     * @param roles The list of roles on the SceneCard.
     */
    SceneCard(String title, String image, int budget, int id, String desc, List<Role> roles) {
        this.title = title;
        this.image = image;
        this.budget = budget;
        this.id = id;
        this.desc = desc;
        this.roles = roles;
    }

    /**
     * Returns the title of the SceneCard.
     *
     * @return The title of the SceneCard.
     */
    String getTitle() {
        return title;
    }

    /**
     * Returns the image on the SceneCard.
     *
     * @return The image on the SceneCard.
     */
    String getImage() {
        return image;
    }

    /**
     * Returns the budget for the SceneCard.
     *
     * @return The budget for the SceneCard.
     */
    int getBudget() {
        return budget;
    }

    /**
     * Returns the unique identifier of the SceneCard.
     *
     * @return The unique identifier of the SceneCard.
     */
    int getID() {
        return id;
    }

    /**
     * Returns the description of the SceneCard.
     *
     * @return The description of the SceneCard.
     */
    String getDesc() {
        return desc;
    }

    /**
     * Returns the list of roles on the SceneCard.
     *
     * @return The list of roles on the SceneCard.
     */
    List<Role> getRoles() {
        return roles;
    }
}
