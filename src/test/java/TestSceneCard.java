import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

class TestSceneCard {
    private List<Role> roles;

    @BeforeEach
    void setUp() {
        roles = new ArrayList<>();
        Area area = new Area(0, 0, 0, 0);
        roles.add(new Role("Role 1", 1, area, "Role 1 line", false));
        roles.add(new Role("Role 2", 2, area, "Role 2 line", true));
    }

    @Test
    void testSceneCardGetID() {
        SceneCard sceneCard = new SceneCard( "The Test Scene", "img/01.png", 1, 1, "Test Scene Description", roles);
        assertEquals(1, sceneCard.getID());
    }

    @Test
    void testSceneCardGetTitle() {
        SceneCard sceneCard = new SceneCard("The Test Scene", "img/01.png", 1, 1, "Test Scene Description", roles);
        assertEquals("The Test Scene", sceneCard.getTitle());
    }

    @Test
    void testSceneCardGetDesc() {
        SceneCard sceneCard = new SceneCard("The Test Scene", "img/01.png", 1, 1, "Test Scene Description", roles);
        assertEquals("Test Scene Description", sceneCard.getDesc());
    }

    @Test
    void testSceneCardGetImage() {
        SceneCard sceneCard = new SceneCard("The Test Scene", "img/01.png", 1, 1, "Test Scene Description", roles);
        assertEquals("img/01.png", sceneCard.getImage());
    }

    @Test
    void testSceneCardGetBudget() {
        SceneCard sceneCard = new SceneCard("The Test Scene", "img/01.png", 1, 1, "Test Scene Description", roles);
        assertEquals(1, sceneCard.getBudget());
    }

    @Test
    void testSceneCardGetRoles() {
        SceneCard sceneCard = new SceneCard("The Test Scene", "img/01.png", 1, 1, "Test Scene Description", roles);
        assertEquals(roles, sceneCard.getRoles());
    }

}