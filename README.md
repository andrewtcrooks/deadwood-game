# Java Program Setup Guide

Welcome to a java version of the game Deadwood by Cheapass Games! This guide will help you set up and run the program using Visual Studio Code (VS Code).

## Prerequisites

Before you start, make sure you have the following installed:
- Git
- Java Development Kit (JDK)
- Visual Studio Code
- Java Extension Pack for Visual Studio Code

## Getting the Code

First, you need to clone the Deadwood game repository from GitHub:

1. Open the terminal.
2. Navigate to the directory where you want to clone the repository.
3. Run `git clone https://github.com/andrewtcrooks/deadwood.git`.
4. Once cloned, open the project folder in Visual Studio Code by running `code deadwood`.

## Project Structure

Our project is organized into several key folders:
- `src`: Contains all Java source files.
- `lib`: Contains required libraries.
- `saved`: Contains saved game files for testing/grading.
- `bin`: The default directory for compiled bytecode files (.class files).

## Compiling the Program

To compile the Java program, follow these steps:
1. Open the project in Visual Studio Code.
2. Open the Command Palette (`Ctrl+Shift+P` on Windows/Linux, `Cmd+Shift+P` on macOS).
3. Type `Java: Compile Workspace` and press Enter. This compiles the source files and places the output in the `bin` folder.

## Running the Program

After compiling, you can run the program within VS Code:
1. Find the Deadwood.java class in the `src` folder and open it.
2. Click on the `Run` button (green triangle) at the top right of the editor window.

Alternatively, you can run the program from the integrated terminal:
1. Open the integrated terminal in VS Code (`Ctrl+`` on Windows/Linux, ``Cmd+`` on macOS).
2. Navigate to the `bin` directory with `cd bin`.
3. Execute the program with `java Deadwood`.

## Accessing the Help Menu

While playing the game, you can access the help menu at any time by typing `help` in the game console. This will display a list of available commands and instructions on how to play the game.