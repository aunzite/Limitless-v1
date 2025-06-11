/////////////////////////////////////////////////////////////////////////////
// Limitless
// SuperObject.java
// Created: May 12, 2025
// Authors: Aun, Ajmal
// 
// Description: Base class for all interactable objects in the game. This class:
// - Defines core object properties and states
// - Manages object rendering and collision
// - Provides a framework for object interaction
// - Supports object image and position management
// - Serves as a parent for all specific game objects
/////////////////////////////////////////////////////////////////////////////

package object;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import main.GamePanel;

// SuperObject is the base class for all objects that can be placed in the game world
public class SuperObject {
    // Object image for rendering
    public BufferedImage image;
    // Object name identifier
    public String name;
    // World position coordinates
    public int worldX, worldY;
    // Collision area for the object
    public Rectangle solidArea = new Rectangle(0, 0, 48, 48);
    // Collision flag
    public boolean collision = false;

    public void draw(Graphics2D g2, GamePanel gp){
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if(worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
           worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
           worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
           worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
            // Draw at half tile size, centered
            int size = gp.tileSize / 2;
            int offset = (gp.tileSize - size) / 2;
            g2.drawImage(image, screenX + offset, screenY + offset, size, size, null);
        }
    }
}   
