/**
 * GameView interface
 */
public interface GameView extends Observer{
    String getPlayerInput();
    int getNumPlayers();
    void showMessage(String message);
    void update(Object arg);
}
