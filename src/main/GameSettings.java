/////////////////////////////////////////////////////////////////////////////
// Limitless
// GameSettings.java
// Created: June 2, 2025
// Authors: Aun, Ajmal
// 
// Description: Manages global game settings and configurations. This class:
// - Handles game-wide settings storage
// - Manages audio and display preferences
// - Controls game difficulty settings
// - Processes user configuration
// - Provides settings persistence
/////////////////////////////////////////////////////////////////////////////

package main;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

// GameSettings class manages global game configuration
public class GameSettings {
    // Singleton instance
    private static GameSettings instance;
    
    // Display settings
    private boolean fullscreen = false;
    private int resolution = 0; // 0: 720p, 1: 1080p, 2: 1440p
    private boolean vsync = true;
    
    // Audio settings
    private float masterVolume = 1.0f;
    private float musicVolume = 1.0f;
    private float sfxVolume = 1.0f;
    
    // Game settings
    private int difficulty = 1; // 0: Easy, 1: Normal, 2: Hard
    private boolean showFPS = false;
    private boolean showMinimap = true;
    
    // Settings file path
    private static final String SETTINGS_FILE = "settings.txt";
    
    // Private constructor for singleton pattern
    private GameSettings() {
        loadSettings();
    }
    
    // Gets singleton instance
    public static GameSettings getInstance() {
        if (instance == null) {
            instance = new GameSettings();
        }
        return instance;
    }
    
    // Loads settings from file
    public void loadSettings() {
        try {
            File file = new File(SETTINGS_FILE);
            if (!file.exists()) {
                saveSettings(); // Create default settings file
                return;
            }
            
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length != 2) continue;
                
                String key = parts[0].trim();
                String value = parts[1].trim();
                
                switch (key) {
                    case "fullscreen":
                        fullscreen = Boolean.parseBoolean(value);
                        break;
                    case "resolution":
                        resolution = Integer.parseInt(value);
                        break;
                    case "vsync":
                        vsync = Boolean.parseBoolean(value);
                        break;
                    case "masterVolume":
                        masterVolume = Float.parseFloat(value);
                        break;
                    case "musicVolume":
                        musicVolume = Float.parseFloat(value);
                        break;
                    case "sfxVolume":
                        sfxVolume = Float.parseFloat(value);
                        break;
                    case "difficulty":
                        difficulty = Integer.parseInt(value);
                        break;
                    case "showFPS":
                        showFPS = Boolean.parseBoolean(value);
                        break;
                    case "showMinimap":
                        showMinimap = Boolean.parseBoolean(value);
                        break;
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Saves settings to file
    public void saveSettings() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(SETTINGS_FILE));
            writer.println("fullscreen=" + fullscreen);
            writer.println("resolution=" + resolution);
            writer.println("vsync=" + vsync);
            writer.println("masterVolume=" + masterVolume);
            writer.println("musicVolume=" + musicVolume);
            writer.println("sfxVolume=" + sfxVolume);
            writer.println("difficulty=" + difficulty);
            writer.println("showFPS=" + showFPS);
            writer.println("showMinimap=" + showMinimap);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Getters and setters for all settings
    public boolean isFullscreen() { return fullscreen; }
    public void setFullscreen(boolean fullscreen) { this.fullscreen = fullscreen; }
    
    public int getResolution() { return resolution; }
    public void setResolution(int resolution) { this.resolution = resolution; }
    
    public boolean isVsync() { return vsync; }
    public void setVsync(boolean vsync) { this.vsync = vsync; }
    
    public float getMasterVolume() { return masterVolume; }
    public void setMasterVolume(float masterVolume) { this.masterVolume = masterVolume; }
    
    public float getMusicVolume() { return musicVolume; }
    public void setMusicVolume(float musicVolume) { this.musicVolume = musicVolume; }
    
    public float getSfxVolume() { return sfxVolume; }
    public void setSfxVolume(float sfxVolume) { this.sfxVolume = sfxVolume; }
    
    public int getDifficulty() { return difficulty; }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }
    
    public boolean isShowFPS() { return showFPS; }
    public void setShowFPS(boolean showFPS) { this.showFPS = showFPS; }
    
    public boolean isShowMinimap() { return showMinimap; }
    public void setShowMinimap(boolean showMinimap) { this.showMinimap = showMinimap; }
    
    public Color adjustColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        
        // Adjust brightness
        hsb[2] = Math.min(1.0f, hsb[2] * brightness);
        
        // Adjust contrast
        hsb[2] = (hsb[2] - 0.5f) * contrast + 0.5f;
        hsb[2] = Math.max(0.0f, Math.min(1.0f, hsb[2]));
        
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }
    
    public void applySettings(Graphics2D g2) {
        // Apply brightness and contrast to the graphics context
        // This is a simple implementation - you might want to use more sophisticated
        // image processing techniques for better results
        float brightnessFactor = brightness - 1.0f;
        float contrastFactor = contrast - 1.0f;
        
        // Create a composite that combines brightness and contrast
        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        
        // Apply brightness
        if (brightnessFactor != 0) {
            g2.setColor(new Color(
                (int)(brightnessFactor * 255),
                (int)(brightnessFactor * 255),
                (int)(brightnessFactor * 255),
                (int)(Math.abs(brightnessFactor) * 255)
            ));
            g2.fillRect(0, 0, g2.getDeviceConfiguration().getBounds().width,
                       g2.getDeviceConfiguration().getBounds().height);
        }
        
        // Apply contrast
        if (contrastFactor != 0) {
            g2.setColor(new Color(
                (int)(contrastFactor * 255),
                (int)(contrastFactor * 255),
                (int)(contrastFactor * 255),
                (int)(Math.abs(contrastFactor) * 255)
            ));
            g2.fillRect(0, 0, g2.getDeviceConfiguration().getBounds().width,
                       g2.getDeviceConfiguration().getBounds().height);
        }
        
        g2.setComposite(originalComposite);
    }
} 