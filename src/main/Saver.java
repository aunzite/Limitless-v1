/////////////////////////////////////////////////////////////////////////////
// Limitless
// Saver.java
// Created: June 1, 2025
// Authors: Aun, Ajmal
// 
// Description: Handles game save and load functionality. This class:
// - Manages save file creation and storage
// - Handles game state serialization
// - Processes save file loading
// - Manages save slots and data
// - Provides save/load error handling
/////////////////////////////////////////////////////////////////////////////

package main;
import java.io.*;
import javax.swing.JFrame;
import entity.Weapon;

// Saver class manages game save and load operations
public class Saver {
    // Game panel reference for state management
    private GamePanel gp;
    
    // Save file directory
    private static final String SAVE_DIR = "saves/";
    
    // Save slot constants
    public static final int SAVE_SLOT_1 = 0;
    public static final int SAVE_SLOT_2 = 1;
    public static final int SAVE_SLOT_3 = 2;
    
    // Current save slot
    private int currentSlot = SAVE_SLOT_1;
    
    // Constructor initializes saver with game panel reference
    public Saver(GamePanel gp) {
        this.gp = gp;
        createSaveDirectory();
    }
    
    // Creates save directory if it doesn't exist
    private void createSaveDirectory() {
        File saveDir = new File(SAVE_DIR);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
    }
    
    // Saves game state to file
    public void saveGame(int slot, int playerX, int playerY) {
        try {
            // Create save data object
            SaveData saveData = new SaveData();
            saveData.playerX = playerX;
            saveData.playerY = playerY;
            saveData.gameState = gp.gameState;
            saveData.inventory = gp.player.inventory;
            
            // Serialize and save data
            FileOutputStream fos = new FileOutputStream(SAVE_DIR + "save" + slot + ".dat");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(saveData);
            oos.close();
            fos.close();
            
            // Show save confirmation
            gp.showMessage("Game saved successfully!");
        } catch (IOException e) {
            gp.showMessage("Error saving game!");
            e.printStackTrace();
        }
    }
    
    // Loads game state from file
    public void loadGame(int slot) {
        try {
            // Load and deserialize data
            FileInputStream fis = new FileInputStream(SAVE_DIR + "save" + slot + ".dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            SaveData saveData = (SaveData) ois.readObject();
            ois.close();
            fis.close();
            
            // Apply loaded data
            gp.player.worldX = saveData.playerX;
            gp.player.worldY = saveData.playerY;
            gp.gameState = saveData.gameState;
            gp.player.inventory = saveData.inventory;
            
            // Show load confirmation
            gp.showMessage("Game loaded successfully!");
        } catch (IOException | ClassNotFoundException e) {
            gp.showMessage("Error loading game!");
            e.printStackTrace();
        }
    }
    
    // Deletes save file
    public void deleteSave(int slot) {
        File saveFile = new File(SAVE_DIR + "save" + slot + ".dat");
        if (saveFile.exists()) {
            saveFile.delete();
            gp.showMessage("Save file deleted!");
        } else {
            gp.showMessage("No save file found!");
        }
    }
    
    // Save data class for serialization
    private static class SaveData implements Serializable {
        private static final long serialVersionUID = 1L;
        public int playerX;
        public int playerY;
        public int gameState;
        public Inventory inventory;
    }
}