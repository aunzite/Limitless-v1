/////////////////////////////////////////////////////////////////////////////
// Limitless
// Main.java
// Created: June 5, 2025
// Authors: Aun, Ajmal
// 
// Description: Entry point for the Limitless game. This class:
// - Initializes the main game window
// - Sets up the game panel and frame
// - Starts the game loop
// - Handles application startup
// - Coordinates initial game setup
/////////////////////////////////////////////////////////////////////////////

package main;

import javax.swing.JFrame;

// Main class launches the game
public class Main {
    public static void main(String[] args) {
        // Create the main game window (JFrame)
        JFrame window = new JFrame("Limitless");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        // Create and add the main game panel
        GamePanel gamePanel = new GamePanel(window);
        window.add(gamePanel);
        window.pack();

        // Center the window on the screen
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        // Start the game loop
        gamePanel.startGameThread();
    }
}