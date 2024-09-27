# Deadwood Game Setup and Testing Guide

This guide provides instructions on how to download, run, and test the Deadwood game from the command line or within Visual Studio Code (VS Code).

## Command Line Interface Prerequisites

Ensure you have the following installed before proceeding:

- Git

  ```
  brew install git
  ```
- Eclipse Temurin Java Development Kit 17 (currently 17.0.12)

  ```
  brew install --cask temurin@17
  ```

## Additional Prerequisites for VS Code (in addition to CLI prereqs)

Not required but highly recommended that you have the following installed before proceeding:

- Visual Studio Code (VS Code)

  ```
  brew install --cask visual-studio-code
  ```
- Java Extension Pack for Visual Studio Code

  Install this through the extensions sidebar in VS Code (looks like 4 small squares)

## Downloading the Program

1. Open a terminal.
2. Navigate to the directory where you want to download the Deadwood game.
3. Clone the repository by running:
   ```
   git clone https://github.com/andrewtcrooks/deadwood-game.git
   ```
4. Change into the cloned directory:
   ```
   cd deadwood-game
   ```

## Running the Program in VS Code

1. Open the project in VS Code:

   ```
   code .
   ```
2. To run the program, follow these steps:

   * Click on the "Run and Debug" sidebar tab on the left side of Visual Studio Code, which looks like a play button with a bug.
   * In the "Run and Debug" view, find the green arrow next to "Launch Deadwood" and click it to start the program.
3. If for some reason step 3 does not work, find the `Deadwood.java` class in the `src/main/java` folder and click on the `Run` button (green triangle) at the top right of the editor window.

## Running the Program from the Command Line

1. Compile the source files:
   ```
   ./gradlew build
   ```
2. Run the application:
   ```
   ./gradlew run
   ```

## How to play

On each players turn, pink translucent clickable areas are added to indicate potential player moves. Scrolling over these areas will popup a tooltip to indicate an action the player can take with the button such as Move, Work, Act, Rehearse, Upgrade, and End.

* **Move** - While not working a role, click on any highlighted neighboring location scene card or neighboring location button (in the case of the Casting Office or Trailer) to be moved to that location.
* **Work** - Click on any highlighted roles at the current location to take the role (you cannot leave the location until the scene card is wrapped).
* **Act** - Click on the highlighted takes (numbers in a circle) at the area to act the next take and if successful then wrap the take. Success depends on rolling a number equal to or greater than the budget listed in upper right corner of the location's scene card. Wrap all the takes at a location to wrap the scene card and get the bonus payout! Bonus payout rolls a number of dice equal to the scene card budget and adds the dice roles to each user who is working a role on a card (not the lcoation itself) in a round-robin fashion starting from the highest rank to the lowest.
* **Rehearse** - Click anywhere on a highlighted scene card, unless the active player is on that card, in which case clicking the user's die on the card will end the turn while clicking anywhere else on the card will rehearse (even other player dice). Rehearsing is important as rehearsing gives you a rehearsal token and each rehearsal token adds to your dice role when you **Act** while working at the current location. Rehearsing sufficiently first reduces the likelihood of the **Act** attempt failing.
* **Upgrade** - At the casting office, upgrade buttons for upgrades the user can afford will appear. Clicking on them upgrades the user to that rank and charges their dollars or credits. Upgrading is important as the final score = ( dollars + credits + 5 * users_rank).
* **End** - Click on the current player's die.

![Project Screenshot](images/screenshot_begingame.png "Beginning of Game")

After taking a role at a location, users must remain at the location until all the takes (the numbers inside the circles at each location) have been wrapped (black clapperboard icon present). Once the scene itself wraps, the cards will be covered with grayscale card backs to indicate that the scene has been wrapped. When all but the last scene has wrapped, the game will proceed to the next day and if there are no more days it will end the game, give the user scores, and declare a winner or winners in the case of a tie.

![Project Screenshot](images/screenshot_endgame.png)

## Offical Game Rules

For more detailed information on how players earn dollars, credits, and tokens by playing location roles and card roles, please refer to the game [documentation](Deadwood-Free-Edition-Rules.pdf).

## Running Unit Tests

### Prerequisites

First you must place your Test*.java files in the src/test/java folder.

### In VS Code

1. Click on the "Testing" sidebar tab on the left side of Visual Studio Code, which looks like an Erlenmeyer flask.
2. In the "Testing" view, hover your cursor over "deadwood" and click on the play button that appears to the right to run all the unit tests.

### From the Command Line

Run the following command to execute all unit tests:

```
./gradlew test
```

This will run all tests and generate a report in `build/reports/tests/test/index.html`. You can open this file in a web browser to view the test results.

## Additional Information

- For more detailed information on running and testing the program within VS Code, refer to the [Java Extension Pack documentation](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack).
- If you encounter any issues with Eclipse Temurin Java Development Kit 17 or setting up the environment, ensure your `JAVA_HOME` environment variable is set correctly to the path of your Eclipse Temurin Java Development Kit 17 installation.
