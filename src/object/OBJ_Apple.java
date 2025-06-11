/////////////////////////////////////////////////////////////////////////////
// Limitless
// OBJ_Apple.java
// Created: May 18, 2025
// Authors: Aun, Ajmal
// 
// Description: Represents an apple object in the game. This class:
// - Extends SuperObject for apple-specific behavior
// - Sets apple image and properties
// - Can be collected by the player
// - Used for health or quest purposes
// - Demonstrates object inheritance
/////////////////////////////////////////////////////////////////////////////

package object;

import javax.imageio.ImageIO;
import java.io.IOException;

// OBJ_Apple defines the apple collectible object
public class OBJ_Apple extends SuperObject {
    public int quantity = 1;

    public OBJ_Apple() {
        this(1);
    }
    public OBJ_Apple(int quantity) {
        name = "Apple";
        this.quantity = quantity;
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/res/objects/apple.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getDescription() {
        String desc = "A fresh, juicy apple that restores your vitality.\nEffect: Restores 20 health and 15 stamina.";
        if (quantity > 1) desc += "\nQuantity: " + quantity;
        return desc;
    }

    public boolean isNear(OBJ_Apple other, int hitboxSize) {
        int dx = this.worldX - other.worldX;
        int dy = this.worldY - other.worldY;
        return Math.abs(dx) < hitboxSize && Math.abs(dy) < hitboxSize;
    }
} 