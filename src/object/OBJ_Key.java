/////////////////////////////////////////////////////////////////////////////
// Limitless
// OBJ_Key.java
// Created: May 22, 2025
// Authors: Aun, Ajmal
// 
// Description: Represents a key object in the game. This class:
// - Extends SuperObject for key-specific behavior
// - Sets key image and properties
// - Used to unlock doors or chests
// - Can be collected by the player
// - Demonstrates object inheritance
/////////////////////////////////////////////////////////////////////////////

package object;

import javax.imageio.ImageIO;
import java.io.IOException;

// OBJ_Key defines the key collectible object
public class OBJ_Key extends SuperObject {
    // Constructor sets up key image and name
    public OBJ_Key() {
        name = "Key";
        try {
            // Load key image from resources
            image = ImageIO.read(getClass().getResourceAsStream("/res/objects/key.png"));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
