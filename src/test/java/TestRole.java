import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TestRole {

    @Test
    void testRoleConstructorDefaults() {
        Area area = new Area(0, 0, 0, 0);
        Role role = new Role("Doctor", 1, area, "I'll heal you", true);
        assertFalse(role.isOccupied());
        assertEquals(null, role.getPlayer());
    }

    @Test
    void testRoleGetName() {
        Area area = new Area(0, 0, 0, 0);
        Role role = new Role("Sheriff", 1, area, "I'm the law!", false);
        assertEquals("Sheriff", role.getName());
    }

    @Test
    void testRoleGetRank() {
        Area area = new Area(0, 0, 0, 0);
        Role role = new Role("Outlaw", 3, area, "Draw, partner!", true);
        assertEquals(3, role.getRank());
    }

    @Test
    void testRoleGetArea() {
        Area area = new Area(0, 0, 0, 0);
        Role role = new Role("Actor", 2, area, "To be or not to be", false);
        assertEquals(area, role.getArea());
    }

    @Test
    void testRoleGetLine() {
        Area area = new Area(0, 0, 0, 0);
        Role role = new Role("Actor", 2, area, "To be or not to be", false);
        assertEquals("To be or not to be", role.getLine());
    }

    @Test
    void testRoleGetPlayer() {
        Area area = new Area(0, 0, 0, 0);
        Role role = new Role("Actor", 2, area, "To be or not to be", false);
        Player player = new Player(1, 0, 0);
        role.assignPlayer(player);
        assertEquals(player, role.getPlayer());
    }
    
    @Test
    void testRoleOnCard() {
        Area area = new Area(0, 0, 0, 0);
        Role role = new Role("Actor", 2, area, "To be or not to be", false);
        assertFalse(role.getOnCard());
    }

    @Test
    void testRoleAssignPlayer() {
        Area area = new Area(0, 0, 0, 0);
        Role role = new Role("Actor", 2, area, "To be or not to be", false);
        Player player = new Player(1, 0, 0);
        role.assignPlayer(player);
        assertTrue(role.isOccupied());
        assertEquals(player, role.getPlayer());
    }

    @Test
    void testRoleRemovePlayer() {
        Area area = new Area(0, 0, 0, 0);
        Role role = new Role("Actor", 2, area, "To be or not to be", false);
        Player player = new Player(1, 0, 0);
        role.assignPlayer(player);
        role.removePlayer();
        assertFalse(role.isOccupied());
        assertEquals(null, role.getPlayer());
    }

}