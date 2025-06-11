/////////////////////////////////////////////////////////////////////////////
// Limitless
// OBJ_Apple.java
// Created: May 18, 2025
// Authors: Aun, Ajmal
// 
// Description: Represents an apple object in the game. This class:
// - Extends Entity for apple-specific behavior
// - Used as a collectible or consumable item
// - Can restore health or be used in quests
// - Demonstrates object inheritance
// - Sets apple image and properties
/////////////////////////////////////////////////////////////////////////////

package entity;

// OBJ_Apple defines the apple collectible entity
public class OBJ_Apple extends Entity {
    public OBJ_Apple() {
        super("Apple", "res/object/apple.png", 1);
    }
    public OBJ_Apple(int quantity) {
        super("Apple", "res/object/apple.png", quantity);
    }
} 