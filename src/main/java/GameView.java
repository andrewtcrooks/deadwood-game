/**
 * GameView interface
 */
public interface GameView extends Observer{
    public String getPlayerInput();
    public int getNumPlayers();
    public void showMessage(String message);
    public void update(String eventType, Object eventData);
    // public void setGameActionListener(GameActionListener listener);
}
