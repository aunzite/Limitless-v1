/////////////////////////////////////////////////////////////////////////////
// Limitless
// TileManager.java
// Created: May 19, 2025
// Authors: Aun, Ajmal
// 
// Description: Manages all tiles and map rendering in the game. This class:
// - Loads and stores tile images and properties
// - Handles map data and tile placement
// - Renders visible tiles to the screen
// - Supports collision and tile type management
// - Coordinates with the game world system
/////////////////////////////////////////////////////////////////////////////

package tile;

import java.awt.Graphics2D;
import java.io.*;
import javax.imageio.ImageIO;
import main.GamePanel;

// TileManager handles loading, storing, and rendering all tiles
public class TileManager {
    // Reference to the main game panel
    GamePanel gp;
    // Array of all tile types
    public Tile[] tile;
    // 2D array for map layout
    public int[][] mapTileNum;

    // Constructor initializes tile manager with game panel
    public TileManager(GamePanel gp) {
        this.gp = gp;
        tile = new Tile[50]; // Example: 50 tile types
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
        loadTileImages();
        loadMap("/res/maps/world01.txt");
    }

    // Loads all tile images and sets collision properties
    public void loadTileImages() {
        try {
            // Example: Load grass tile
            tile[0] = new Tile(ImageIO.read(getClass().getResourceAsStream("/res/tiles/grass.png")), false);
            // Example: Load water tile (solid)
            tile[1] = new Tile(ImageIO.read(getClass().getResourceAsStream("/res/tiles/water.png")), true);
            // Add more tiles as needed...
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    // Loads map data from a file
    public void loadMap(String filePath) {
        // Implementation for loading map data from file
        // (Omitted for brevity)
    }

    // Renders visible tiles to the screen
    public void draw(Graphics2D g2) {
        // Loop through visible map area and draw each tile
        for(int col = 0; col < gp.maxWorldCol; col++) {
            for(int row = 0; row < gp.maxWorldRow; row++) {
                int tileNum = mapTileNum[col][row];
                int x = col * gp.tileSize;
                int y = row * gp.tileSize;
                g2.drawImage(tile[tileNum].image, x, y, gp.tileSize, gp.tileSize, null);
            }
        }
    }
}