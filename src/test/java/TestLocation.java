import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

public class TestLocation {

    private Area area;
    private List<Take> takes;
    private List<Role> roles;
    private String name;
    private List<String> neighbors;

    @BeforeEach
    public void setUp() {
        area = new Area(1, 2, 3, 4);
        takes = new ArrayList<>();
        takes.add(new Take(3, new Area(5, 6, 7, 8)));
        roles = new ArrayList<>();
        roles.add(new Role("Role 1", 1, new Area(9, 10, 11, 12), "Line 1", false));
        name = "Test Location";
        neighbors = new ArrayList<>();
        neighbors.add("Neighbor 1");
    }

    @Test
    public void testLocationConstructor() {
        Location location = new Location(name, neighbors, area, takes, roles);
        assertEquals(name, location.getName());
        assertEquals(neighbors, location.getNeighbors());
        assertEquals(area, location.getArea());
        assertEquals(takes, location.getTakes());
        assertEquals(roles, location.getRoles());
    }

    @Test
    public void testGetName() {
        Location location = new Location(name, neighbors, area, takes, roles);
        assertEquals(name, location.getName());
    }
    
    @Test
    public void testGetNeighbors() {
        Location location = new Location(name, neighbors, area, takes, roles);
        assertEquals(neighbors, location.getNeighbors());
    }
    
    @Test
    public void testGetArea() {
        Location location = new Location(name, neighbors, area, takes, roles);
        assertEquals(area, location.getArea());
    }

    @Test
    public void testGetTakes() {
        Location location = new Location(name, neighbors, area, takes, roles);
        assertEquals(takes, location.getTakes());
    }

    @Test
    public void testGetRoles() {
        Location location = new Location(name, neighbors, area, takes, roles);
        assertEquals(roles, location.getRoles());
    }

    @Test
    public void testGetScene() {
        Location location = new Location(name, neighbors, area, takes, roles);
        assertNull(location.getSceneCard());
    }

    @Test
    public void testGetShots() {
        Location location = new Location(name, neighbors, area, takes, roles);
        assertEquals(1, location.getShots());
    }
    
    @Test
    public void testGetAllRoles() {
        Location location = new Location(name, neighbors, area, takes, roles);
        assertEquals(roles, location.getRoles());
    }

    @Test
    public void testSetSceneCard() {
        Location location = new Location(name, neighbors, area, takes, roles);
        SceneCard scene = new SceneCard("Scene 1", "img.png", 1, 7, "Scene Description", roles);
        location.setSceneCard(scene);
        assertEquals(scene, location.getSceneCard());
    }

}