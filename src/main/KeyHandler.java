/////////////////////////////////////////////////////////////////////////////
// Limitless
// KeyHandler.java
// Created: May 15, 2025
// Authors: Aun, Ajmal
// 
// Description: Handles keyboard input and controls for the game. This class:
// - Manages keyboard event processing and state tracking
// - Controls player movement and actions
// - Handles menu navigation and interaction
// - Processes game state changes based on input
// - Coordinates with other game systems for input response
/////////////////////////////////////////////////////////////////////////////

package main;

import entity.HUD;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

// Handles keyboard input for the game by implementing KeyListener interface
// Tracks the state of movement keys (WASD), Enter, and Shift modifiers
public class KeyHandler implements KeyListener {

    private Saver saver;
    private HUD hud;
    private GamePanel gp;

    // Movement & input tracking 
    public boolean shiftPressed;     // Sprint modifier key state
    public boolean upPressed;        // W key state for upward movement
    public boolean downPressed;      // S key state for downward movement
    public boolean leftPressed;      // A key state for left movement
    public boolean rightPressed;     // D key state for right movement
    public boolean enterPressed;     // Enter key state for dialogue advancing (Aun)
    public boolean ePressed;         // E key state for NPC interaction
    public boolean f11Pressed;       // F11 key state for fullscreen toggle
    public boolean escapePressed;    // Escape key state for quitting
    
    // Add new button states
    public boolean savePressed;    // F5 key for saving
    public boolean loadPressed;    // F6 key for loading
    public boolean deletePressed;  // F7 key for deleting save
    public boolean iPressed;       // I key for inventory
    public boolean spacePressed;   // Space key for attack

    // Debug key states
    public boolean debugPressed;

    public KeyHandler(Saver saver, HUD hud) {
        this.saver = saver;
        this.hud = hud;
    }

    public void setHUD(HUD hud) {
        this.hud = hud;
    }

    public void setGamePanel(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    // Required by KeyListener interface but unused in this implementation
    public void keyTyped(KeyEvent e) {
        // Not used (Ahmed)
    }

    @Override
    // Handles key press events and updates input flags
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // Toggle input flags based on key pressed
        switch(code) {
            case KeyEvent.VK_W -> upPressed = true;
            case KeyEvent.VK_S -> downPressed = true;
            case KeyEvent.VK_A -> leftPressed = true;
            case KeyEvent.VK_D -> rightPressed = true;
            case KeyEvent.VK_SHIFT -> shiftPressed = true;
            case KeyEvent.VK_ENTER -> enterPressed = true;  // Dialogue confirm (Ahmed)
            case KeyEvent.VK_E -> ePressed = true;         // NPC interaction
            case KeyEvent.VK_F11 -> f11Pressed = true;     // Fullscreen toggle
            case KeyEvent.VK_ESCAPE -> escapePressed = true; // Escape key for quitting
            case KeyEvent.VK_F5 -> savePressed = true;
            case KeyEvent.VK_F6 -> loadPressed = true;
            case KeyEvent.VK_F7 -> deletePressed = true;
            case KeyEvent.VK_H -> hud.setShowAttackHistory(!hud.isShowAttackHistory()); //Attack history (Ahmed)
            case KeyEvent.VK_I -> iPressed = true;         // Inventory toggle
            case KeyEvent.VK_SPACE -> { spacePressed = true; if (gp != null && gp.player != null) gp.player.handleSpacePressed(); }
            case KeyEvent.VK_F3 -> debugPressed = true;
        }
        saver.handleInput(savePressed, loadPressed, deletePressed);
    }

    @Override
    // Handles key release events and resets input flags
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        switch(code) {
            case KeyEvent.VK_W -> upPressed = false;
            case KeyEvent.VK_S -> downPressed = false;
            case KeyEvent.VK_A -> leftPressed = false;
            case KeyEvent.VK_D -> rightPressed = false;
            case KeyEvent.VK_SHIFT -> shiftPressed = false;
            case KeyEvent.VK_ENTER -> enterPressed = false; // Dialogue released (Ahmed)
            case KeyEvent.VK_E -> ePressed = false;        // NPC interaction released
            case KeyEvent.VK_F11 -> f11Pressed = false;    // Fullscreen toggle released
            case KeyEvent.VK_ESCAPE -> escapePressed = false; // Escape key released
            case KeyEvent.VK_F5 -> savePressed = false;
            case KeyEvent.VK_F6 -> loadPressed = false;
            case KeyEvent.VK_F7 -> deletePressed = false; 
            case KeyEvent.VK_I -> iPressed = false;        // Inventory toggle released
            case KeyEvent.VK_SPACE -> spacePressed = false;
            case KeyEvent.VK_F3 -> debugPressed = false;
        }
        saver.handleInput(savePressed, loadPressed, deletePressed);
    }
}
