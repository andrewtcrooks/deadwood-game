# Deadwood Game Setup and Testing Guide

This guide provides instructions on how to download, run, and test the Deadwood game from the command line or within Visual Studio Code (VS Code).

## Command Line Interface Prerequisites

Ensure you have the following installed before proceeding:

- Git

  ```
  brew install git
  ```
- OpenJDK 8.0

  ```
  brew install --cask corretto@8
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
- If you encounter any issues with OpenJDK 8.0 or setting up the environment, ensure your `JAVA_HOME` environment variable is set correctly to the path of your OpenJDK 8.0 installation.
