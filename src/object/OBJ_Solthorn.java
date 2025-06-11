/////////////////////////////////////////////////////////////////////////////
// Limitless
// OBJ_Solthorn.java
// Created: May 28, 2025
// Authors: Aun, Ajmal
// 
// Description: Represents the Solthorn object in the game. This class:
// - Extends SuperObject for Solthorn-specific behavior
// - Sets Solthorn image and properties
// - Used for special events or quests
// - Can be collected or interacted with
// - Demonstrates object inheritance
/////////////////////////////////////////////////////////////////////////////

package object;

import javax.imageio.ImageIO;
import java.io.IOException;

// OBJ_Solthorn defines the Solthorn special object
public class OBJ_Solthorn extends SuperObject {
    // Constructor sets up Solthorn image and name
    public OBJ_Solthorn() {
        name = "Solthorn";
        try {
            // Load Solthorn image from resources
            image = ImageIO.read(getClass().getResourceAsStream("/res/objects/solthorn.png"));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public String getDescription() {
        return "Solthorn\nA legendary blade passed down through Elaria's bloodline, forged around a gem said to hold unimaginable power.";
    }
} 