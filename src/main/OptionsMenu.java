/////////////////////////////////////////////////////////////////////////////
// Limitless
// OptionsMenu.java
// Created: May 25, 2025
// Authors: Aun, Ajmal
// 
// Description: Manages the game's options and settings menu. This class:
// - Handles display and audio settings
// - Manages control configurations
// - Processes user preferences
// - Saves and loads settings
// - Provides interface for game customization
/////////////////////////////////////////////////////////////////////////////

package main;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class OptionsMenu implements MouseListener, MouseMotionListener {
    private GamePanel gp;
    private GameSettings settings;
    private int selectedOption = 0;
    private int hoveredOption = -1;
    private float[] optionScales;
    private static final float SCALE_SPEED = 0.1f;
    private static final float MAX_SCALE = 1.2f;
    private boolean isBackButtonHovered = false;
    
    // UI elements
    private Rectangle backButton;
    private Rectangle autoSaveToggle;
    private BufferedImage backgroundImage;
    
    // Fonts
    private Font titleFont = new Font("Comic Sans MS", Font.BOLD, 72);
    private Font menuFont = new Font("Comic Sans MS", Font.PLAIN, 36);
    
    private GifImage backgroundGif;
    
    // Settings categories
    public static final int DISPLAY = 0;
    public static final int AUDIO = 1;
    public static final int CONTROLS = 2;
    public static final int BACK = 3;
    
    // Current settings category
    private int currentCategory = DISPLAY;
    
    // Selection index for current category
    private int selectionIndex = 0;
    
    // Settings options arrays
    private String[] displayOptions = {"Fullscreen", "Resolution", "VSync", "Back"};
    private String[] audioOptions = {"Master Volume", "Music Volume", "SFX Volume", "Back"};
    private String[] controlOptions = {"Key Bindings", "Controller", "Back"};
    
    public OptionsMenu(GamePanel gp) {
        this.gp = gp;
        this.settings = GameSettings.getInstance();
        
        // Initialize fonts
        titleFont = new Font("Comic Sans MS", Font.BOLD, 72);
        menuFont = new Font("Comic Sans MS", Font.PLAIN, 36);
        
        // Initialize option scales
        optionScales = new float[1]; // Auto-save
        for (int i = 0; i < optionScales.length; i++) {
            optionScales[i] = 1.0f;
        }
        
        // Initialize auto-save toggle
        int toggleWidth = 100;
        int toggleHeight = 50;
        int toggleX = gp.screenWidth / 2 - toggleWidth / 2;
        int toggleY = gp.screenHeight / 2;
        autoSaveToggle = new Rectangle(toggleX, toggleY, toggleWidth, toggleHeight);
        
        // Initialize back button
        backButton = new Rectangle(gp.screenWidth / 2 - 100, gp.screenHeight - 100, 200, 50);
        
        // Add mouse listeners
        gp.addMouseListener(this);
        gp.addMouseMotionListener(this);
        
        // Load background GIF (same as Menu)
        try {
            backgroundGif = new GifImage("res/menu/menu.gif");
        } catch (Exception e) {
            System.err.println("Error loading background GIF in OptionsMenu: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void update() {
        // Update scales for hover effect
        for (int i = 0; i < optionScales.length; i++) {
            if (i == hoveredOption) {
                optionScales[i] = Math.min(MAX_SCALE, optionScales[i] + SCALE_SPEED);
            } else {
                optionScales[i] = Math.max(1.0f, optionScales[i] - SCALE_SPEED);
            }
        }
        
        // Handle escape key to go back
        if (gp.keyH.escapePressed) {
            gp.gameState = GamePanel.MENU_STATE;
            gp.keyH.escapePressed = false;
        }
        
        // Process menu navigation
        if(gp.keyH.upPressed) {
            selectionIndex--;
            if(selectionIndex < 0) {
                selectionIndex = getCurrentOptions().length - 1;
            }
        }
        if(gp.keyH.downPressed) {
            selectionIndex++;
            if(selectionIndex >= getCurrentOptions().length) {
                selectionIndex = 0;
            }
        }
        
        // Process menu selection
        if(gp.keyH.enterPressed) {
            handleSelection();
        }
    }
    
    public void draw(Graphics2D g2) {
        // Draw background GIF
        if (backgroundGif != null) {
            BufferedImage currentFrame = backgroundGif.getCurrentFrame();
            if (currentFrame != null) {
                // Scale to fit the screen
                double scaleX = (double) gp.screenWidth / currentFrame.getWidth();
                double scaleY = (double) gp.screenHeight / currentFrame.getHeight();
                double scale = Math.max(scaleX, scaleY);
                int scaledWidth = (int) (currentFrame.getWidth() * scale);
                int scaledHeight = (int) (currentFrame.getHeight() * scale);
                int x = (gp.screenWidth - scaledWidth) / 2;
                int y = (gp.screenHeight - scaledHeight) / 2;
                g2.drawImage(currentFrame, x, y, scaledWidth, scaledHeight, null);
            }
        } else {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        }
        // Draw a darker semi-transparent overlay
        g2.setColor(new Color(0, 0, 0, 180)); // More opaque than menu
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        
        // Draw title
        g2.setFont(titleFont);
        String title = "OPTIONS";
        FontMetrics fm = g2.getFontMetrics();
        int titleX = (gp.screenWidth - fm.stringWidth(title)) / 2;
        int titleY = gp.screenHeight / 6;
        
        // Draw title with shadow
        g2.setColor(Color.BLACK);
        g2.drawString(title, titleX + 4, titleY + 4);
        g2.setColor(Color.WHITE);
        g2.drawString(title, titleX, titleY);
        
        // Draw options
        g2.setFont(menuFont);
        fm = g2.getFontMetrics();
        
        // Draw auto-save toggle
        drawAutoSaveToggle(g2);
        
        // Draw back button
        drawBackButton(g2);
        
        // Draw current category options
        String[] options = getCurrentOptions();
        for(int i = 0; i < options.length; i++) {
            String text = options[i];
            int x = getXForCenteredText(text, g2);
            int y = gp.screenHeight/2 + i * 50;
            
            // Highlight selected option
            if(i == selectionIndex) {
                g2.setColor(Color.YELLOW);
            } else {
                g2.setColor(Color.WHITE);
            }
            
            g2.drawString(text, x, y);
        }
    }
    
    private void drawAutoSaveToggle(Graphics2D g2) {
        String label = "Auto Save";
        FontMetrics fm = g2.getFontMetrics();
        int labelX = gp.screenWidth / 4;
        int labelY = gp.screenHeight / 2 + fm.getAscent() / 2;
        
        // Draw label
        g2.setColor(Color.WHITE);
        g2.drawString(label, labelX, labelY);
        
        // Draw toggle button with hover effect
        if (hoveredOption == 0) {
            g2.setColor(new Color(80, 80, 80));
        } else {
            g2.setColor(new Color(60, 60, 60));
        }
        g2.fillRoundRect(autoSaveToggle.x, autoSaveToggle.y, autoSaveToggle.width, autoSaveToggle.height, 10, 10);
        
        // Draw toggle button border
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(autoSaveToggle.x, autoSaveToggle.y, autoSaveToggle.width, autoSaveToggle.height, 10, 10);
        
        // Draw toggle state with hover effect
        String state = settings.isAutoSaveEnabled() ? "ON" : "OFF";
        if (hoveredOption == 0) {
            g2.setColor(new Color(200, 200, 255));
        } else {
            g2.setColor(Color.WHITE);
        }
        int stateX = autoSaveToggle.x + (autoSaveToggle.width - fm.stringWidth(state)) / 2;
        int stateY = autoSaveToggle.y + (autoSaveToggle.height - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(state, stateX, stateY);
    }
    
    private void drawBackButton(Graphics2D g2) {
        String label = "Back";
        FontMetrics fm = g2.getFontMetrics();
        
        // Draw button background with hover effect
        if (isBackButtonHovered) {
            g2.setColor(new Color(80, 80, 80));
        } else {
            g2.setColor(new Color(60, 60, 60));
        }
        g2.fillRoundRect(backButton.x, backButton.y, backButton.width, backButton.height, 10, 10);
        
        // Draw button border
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(backButton.x, backButton.y, backButton.width, backButton.height, 10, 10);
        
        // Draw text with hover effect
        if (isBackButtonHovered) {
            g2.setColor(new Color(200, 200, 255));
        } else {
            g2.setColor(Color.WHITE);
        }
        int textX = backButton.x + (backButton.width - fm.stringWidth(label)) / 2;
        int textY = backButton.y + (backButton.height - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(label, textX, textY);
    }
    
    // MouseListener methods
    @Override
    public void mouseClicked(MouseEvent e) {
        if (gp.gameState != GamePanel.OPTIONS_STATE) return;
        
        Point mousePoint = e.getPoint();
        
        // Check back button
        if (backButton.contains(mousePoint)) {
            gp.gameState = GamePanel.MENU_STATE;
            return;
        }
        
        // Check auto-save toggle
        if (autoSaveToggle.contains(mousePoint)) {
            settings.setAutoSaveEnabled(!settings.isAutoSaveEnabled());
        }
    }
    
    @Override
    public void mousePressed(MouseEvent e) {}
    
    @Override
    public void mouseReleased(MouseEvent e) {}
    
    @Override
    public void mouseEntered(MouseEvent e) {}
    
    @Override
    public void mouseExited(MouseEvent e) {
        if (gp.gameState != GamePanel.OPTIONS_STATE) return;
        hoveredOption = -1;
    }
    
    // MouseMotionListener methods
    @Override
    public void mouseMoved(MouseEvent e) {
        if (gp.gameState != GamePanel.OPTIONS_STATE) return;
        
        Point mousePoint = e.getPoint();
        isBackButtonHovered = backButton.contains(mousePoint);
        
        // Check which option is being hovered
        if (autoSaveToggle.contains(mousePoint)) {
            hoveredOption = 0;
        } else {
            hoveredOption = -1;
        }
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {}
    
    // Helper method to center text horizontally
    private int getXForCenteredText(String text, Graphics2D g2) {
        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return gp.screenWidth/2 - length/2;
    }
    
    // Gets current options array based on category
    private String[] getCurrentOptions() {
        switch(currentCategory) {
            case DISPLAY: return displayOptions;
            case AUDIO: return audioOptions;
            case CONTROLS: return controlOptions;
            default: return displayOptions;
        }
    }
    
    // Handles option selection
    private void handleSelection() {
        String[] options = getCurrentOptions();
        String selected = options[selectionIndex];
        
        if(selected.equals("Back")) {
            currentCategory = DISPLAY;
            selectionIndex = 0;
            return;
        }
        
        // Process selected option
        switch(currentCategory) {
            case DISPLAY:
                handleDisplayOption(selected);
                break;
            case AUDIO:
                handleAudioOption(selected);
                break;
            case CONTROLS:
                handleControlOption(selected);
                break;
        }
    }
    
    // Handles display option selection
    private void handleDisplayOption(String option) {
        switch(option) {
            case "Fullscreen":
                gp.toggleFullscreen();
                break;
            case "Resolution":
                // Handle resolution change
                break;
            case "VSync":
                // Toggle VSync
                break;
        }
    }
    
    // Handles audio option selection
    private void handleAudioOption(String option) {
        switch(option) {
            case "Master Volume":
                // Adjust master volume
                break;
            case "Music Volume":
                // Adjust music volume
                break;
            case "SFX Volume":
                // Adjust SFX volume
                break;
        }
    }
    
    // Handles control option selection
    private void handleControlOption(String option) {
        switch(option) {
            case "Key Bindings":
                // Show key binding menu
                break;
            case "Controller":
                // Show controller settings
                break;
        }
    }
} 