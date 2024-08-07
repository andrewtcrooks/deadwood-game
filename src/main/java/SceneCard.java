import java.util.*;

/**
 * Represents a scene card with an ID, title, description, image, budget, and roles.
 */
public class SceneCard {
    private int id;
    private String title;
    private String desc; // description
    private String image;
    private int budget;
    private List<Role> roles;


    /**
     * Initializes a new SceneCard with the given ID, title, description, image, budget, and roles.
     *
     * @param title The title of the SceneCard (i.e. "Evil Wears a Hat").
     * @param image The image on the SceneCard. (i.e. "01.png")
     * @param budget The budget for the SceneCard.
     * @param id The unique identifier of the SceneCard.
     * @param desc The description of the SceneCard.
     * @param roles The list of roles on the SceneCard.
     */
    public SceneCard(String title, String image, int budget, int id, String desc, List<Role> roles) {
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
    public String getTitle() {
        return this.title;
    }

    /**
     * Returns the image on the SceneCard.
     *
     * @return The image on the SceneCard.
     */
    public String getImage() {
        return this.image;
    }

    /**
     * Returns the budget for the SceneCard.
     *
     * @return The budget for the SceneCard.
     */
    public int getBudget() {
        return this.budget;
    }

    /**
     * Returns the unique identifier of the SceneCard.
     *
     * @return The unique identifier of the SceneCard.
     */
    public int getID() {
        return this.id;
    }

    /**
     * Returns the description of the SceneCard.
     *
     * @return The description of the SceneCard.
     */
    public String getDesc() {
        return this.desc;
    }

    /**
     * Returns the list of roles on the SceneCard.
     *
     * @return The list of roles on the SceneCard.
     */
    public List<Role> getRoles() {
        return this.roles;
    }

}
