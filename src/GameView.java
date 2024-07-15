/**
 * GameView interface
 */
public interface GameView extends Observer{
    void update(Object arg);
    int getNumPlayers();
    String getPlayerInput();
    void showMessage(String message);
}
