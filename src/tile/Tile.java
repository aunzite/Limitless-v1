/////////////////////////////////////////////////////////////////////////////
// Limitless
// Tile.java
// Created: May 14, 2025
// Authors: Aun, Ajmal
// 
// Description: Represents a single tile in the game world. This class:
// - Stores tile image and collision state
// - Used for map rendering and collision detection
// - Supports different tile types (e.g., grass, water)
// - Provides tile property management
// - Serves as a building block for the world
/////////////////////////////////////////////////////////////////////////////

package tile;

import java.awt.image.BufferedImage;

// Tile class represents a single map tile
public class Tile {
    // Image for the tile
    public BufferedImage image;
    // Collision flag (true if tile is solid)
    public boolean collision = false;

    // Constructor for a tile with image and collision state
    public Tile(BufferedImage image, boolean collision) {
        this.image = image;
        this.collision = collision;
    }
}