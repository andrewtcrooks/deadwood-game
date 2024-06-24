import java.util.List;

public interface GameView extends Observer{

    void update(Object arg);

    int getNumPlayers();
    String getPlayerInput();

    void displayWelcomeMessage();

    void showMessage(String message);

    // Invalid Commands
    void displayInvalidCommand();
    void displayInvalidCommandReenter();
    void displayInvalidLocation();
    void displayInvalidRole();
    void displayPlayerRankTooLow(int playerRank, int roleRank);
    void displayInvalidUpgrade();

    // Player Info
    void displayPlayerInfo(int id, int rank, int money, int credits, int rehearsalTokens);

    // Player Location
    void displayPlayerLocation(String locationName);
    void displayPlayerLocation(String locationName, boolean isSceneWrapped, String sceneTitle, int sceneID);
    
    // Board
    void displayBoard(List<Player> players);

    // Player Move
    void displayPlayerMoveOptions(List<String> neighbors);
    void displayPlayerMove(String startLocation, String endLocation);
    void displayPlayerAlreadyMoved();
    void displayPlayerCannotMove();

    // Player Upgrade
    void displayNotInOfficeMessage(Player player);
    void displayUpgradeOptions(Player player);
    void displayDualPaymentOptions(Player player);
    void displayPlayerAlreadyUpgraded();
    void displayUpgradeSuccess(int chosenRank);
    void displayUpgradeFailure();

    // Player Work
    void displayPlayerRole(Player player);
    void displayWorkOptions(Player player);
    void displayPlayerAlreadyHasRole(Player player);
    void displayPlayerHasNoRole();

    // Player Rehearse
    void displayPlayerRehearsed(Player player);

    // Player Act
    void displayAct(Player player);
    void displayActSuccess(int roll);
    void displayActFail(int roll);

    // Player Save Game
    void displaySaveGameCurrentMessage();
    void displaySaveGameInputMessage();
    void displaySaveGameFiles();
    void displaySaveGameMustBeUnique();
    void displaySaveGameDoesNotExist();

    // Player Load Game
    void displayLoadGameInputMessage();

    // Player Help Options
    void displayPlayerOptions();

}
