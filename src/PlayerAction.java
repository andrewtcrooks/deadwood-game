public interface PlayerAction {
    boolean validate(Player player, GameModel model, GameView view);
    boolean execute(Player player, GameModel model, GameView view);
}
