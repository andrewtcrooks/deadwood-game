/**
 * PlayerAction interface
 */
public interface PlayerAction {
    public boolean validate(Player player, GameModel model, GameView view);
    public boolean execute(Player player, GameModel model, GameView view);
}
