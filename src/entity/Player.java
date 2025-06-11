/////////////////////////////////////////////////////////////////////////////
// Limitless
// Player.java
// Created: May 21, 2025
// Authors: Aun, Ajmal
// 
// Description: Manages the player character and their interactions. This class:
// - Handles player movement and controls
// - Manages player stats and inventory
// - Controls combat and interactions
// - Processes player animations
// - Coordinates with game systems
/////////////////////////////////////////////////////////////////////////////

package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import main.GamePanel;
import main.KeyHandler;

// Player class representing the main character in the game
// Extends the Entity class to inherit basic entity properties
public final class Player extends Entity{
    GamePanel gp;       // Reference to the GamePanel instance
    KeyHandler keyH;    // Handles keyboard input

    // Attributes (Ahmed)
    public int hp;
    public int stamina;
    public Weapon weapon;
    public Inventory inventory; // Add inventory field

    // Screen position constants (center of screen)
    public final int screenX; // Fixed X position on screen
    public final int screenY; // Fixed Y position on screen

    // Animation state
    public String animationState = "idle"; // Can be "idle", "walk", or "run"

    // Run animation frames (10-17 for each direction)
    public BufferedImage up10, up11, up12, up13, up14, up15, up16, up17;
    public BufferedImage down10, down11, down12, down13, down14, down15, down16, down17;
    public BufferedImage left10, left11, left12, left13, left14, left15, left16, left17;
    public BufferedImage right10, right11, right12, right13, right14, right15, right16, right17;

    // Store last collided tile(s) for debug drawing
    public java.util.List<int[]> lastCollisionTiles = new java.util.ArrayList<>();
    public void setLastCollisionTile(int col, int row) {
        lastCollisionTiles.add(new int[]{col, row});
    }

    // Slash animation fields
    private BufferedImage[][][] slashSheets = new BufferedImage[1][][]; // [sheet][row][col]
    private int currentSlash = 0;
    private boolean isSlashing = false;
    private long lastSlashTime = 0;
    private static final long SLASH_COOLDOWN = 500; // 500ms cooldown between slashes
    private static final long SLASH_FRAME_DURATION = 50; // 50ms per frame
    private long lastFrameTime = 0;
    private int currentFrame = 0;
    private int totalFrames = 24; // Total frames in slash3.png (6 columns × 4 rows)

    // Constructor initializes player with game panel and keyboard handler
    public Player (GamePanel gp, KeyHandler keyH){
        this.gp = gp;
        this.keyH = keyH;

        hp = 100;
        stamina = 100;
        weapon = null; // Start with no weapon
        inventory = new Inventory(gp); // Use new Inventory constructor

        // Calculate center position of screen for player
        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);
       
        // Make collision box smaller and more centered
        playerHitbox = new Rectangle(28, 16, 24, 48);

        setDefaultValues();
        getPlayerImage();
    }

    // Sets default values for player position and movement
    public void setDefaultValues() {
        // Set initial world position further up and to the left near the ruins and NPC
        worldX = gp.tileSize * 12;  // Move left (was 36)
        worldY = gp.tileSize * 10;  // Move up (was 26)
        speed = 4;              // Default movement speed
        direction = "down";     // Default facing direction
    }
    public int getWorldX() {
        return worldX;
    }
    public int getWorldY() {
        return worldY;
    }
    public String getDirection() {
        return direction;
    }
    // Sets values for player position and movement
    public void setValues (int worldX, int worldY, String direction){

        this.worldX = worldX;
        this.worldY = worldY;
        this.direction = direction;     
    }

    //Extracts a single sprite from the sprite sheet
    private BufferedImage getSprite(String sheetPath, int row, int col, int spriteWidth, int spriteHeight, int offsetX, int offsetY) {
        try {
            BufferedImage spriteSheet = ImageIO.read(new File(sheetPath));
            if (spriteSheet == null) {
                return null;
            }

            int colGap = 30;
            int rowGap = 10;
            // Calculate position in sprite sheet with gaps
            int x = offsetX + col * (spriteWidth + colGap);
            int y = offsetY + row * (spriteHeight + rowGap);

            // Ensure we don't go out of bounds
            if (x + spriteWidth > spriteSheet.getWidth() || y + spriteHeight > spriteSheet.getHeight()) {
                return null;
            }

            return spriteSheet.getSubimage(x, y, spriteWidth, spriteHeight);
        } catch (IOException e) {
            return null;
        }
    }

    // Helper for idle sprites using exact pixel offsets
    private BufferedImage getIdleSprite(String sheetPath, int x, int y, int width, int height) {
        try {
            BufferedImage spriteSheet = ImageIO.read(new File(sheetPath));
            if (spriteSheet == null) return null;
            if (x + width > spriteSheet.getWidth() || y + height > spriteSheet.getHeight()) return null;
            return spriteSheet.getSubimage(x, y, width, height);
        } catch (IOException e) {
            return null;
        }
    }

    // Helper for walk sprites using exact pixel offsets
    private BufferedImage getWalkSprite(String sheetPath, int x, int y) {
        try {
            BufferedImage spriteSheet = ImageIO.read(new File(sheetPath));
            if (spriteSheet == null) return null;
            if (x + 64 > spriteSheet.getWidth() || y + 64 > spriteSheet.getHeight()) return null;
            return spriteSheet.getSubimage(x, y, 64, 64);
        } catch (IOException e) {
            return null;
        }
    }

    // Helper for run sprites using exact pixel offsets
    private BufferedImage getRunSprite(String sheetPath, int x, int y) {
        try {
            BufferedImage spriteSheet = ImageIO.read(new File(sheetPath));
            if (spriteSheet == null) return null;
            if (x + 64 > spriteSheet.getWidth() || y + 64 > spriteSheet.getHeight()) return null;
            return spriteSheet.getSubimage(x, y, 64, 64);
        } catch (IOException e) {
            return null;
        }
    }

    // Loads all player sprites from the sprite sheets
    public void getPlayerImage() {
        getPlayerImageFromDir("res/player/");
    }

    public void getPlayerImageFromDir(String baseDir) {
        try {
            // Sprite sheet configuration
            String idleSheetPath = baseDir + "idle.png";
            String walkSheetPath = baseDir + "walk.png";
            String runSheetPath = baseDir + "run.png";
            int walkSpriteWidth = 29;
            int walkSpriteHeight = 53;
            int walkOffsetX = 15;
            int walkOffsetY = 10;
            int runSpriteWidth = 32;   // Placeholder, update after you provide run details
            int runSpriteHeight = 32;
            int runOffsetX = 0;
            int runOffsetY = 0;
            
            // Load idle animations using exact offsets and 64x64 size
            up1    = getIdleSprite(idleSheetPath, 0,   0,   64, 64);
            up2    = getIdleSprite(idleSheetPath, 64,  0,   64, 64);
            left1  = getIdleSprite(idleSheetPath, 0,   64,  64, 64);
            left2  = getIdleSprite(idleSheetPath, 64,  64,  64, 64);
            down1  = getIdleSprite(idleSheetPath, 0,   128, 64, 64);
            down2  = getIdleSprite(idleSheetPath, 64,  128, 64, 64);
            right1 = getIdleSprite(idleSheetPath, 0,   192, 64, 64);
            right2 = getIdleSprite(idleSheetPath, 64,  192, 64, 64);
            
            // Load walk animations using exact offsets and 64x64 size
            // Up (row 1)
            up5 = getWalkSprite(walkSheetPath, 0, 0);
            up6 = getWalkSprite(walkSheetPath, 64, 0);
            up7 = getWalkSprite(walkSheetPath, 128, 0);
            up8 = getWalkSprite(walkSheetPath, 192, 0);
            up9 = getWalkSprite(walkSheetPath, 256, 0);
            // Left (row 2)
            left5 = getWalkSprite(walkSheetPath, 0, 64);
            left6 = getWalkSprite(walkSheetPath, 64, 64);
            left7 = getWalkSprite(walkSheetPath, 128, 64);
            left8 = getWalkSprite(walkSheetPath, 192, 64);
            left9 = getWalkSprite(walkSheetPath, 256, 64);
            // Down (row 3)
            down5 = getWalkSprite(walkSheetPath, 0, 128);
            down6 = getWalkSprite(walkSheetPath, 64, 128);
            down7 = getWalkSprite(walkSheetPath, 128, 128);
            down8 = getWalkSprite(walkSheetPath, 192, 128);
            down9 = getWalkSprite(walkSheetPath, 256, 128);
            // Right (row 4)
            right5 = getWalkSprite(walkSheetPath, 0, 192);
            right6 = getWalkSprite(walkSheetPath, 64, 192);
            right7 = getWalkSprite(walkSheetPath, 128, 192);
            right8 = getWalkSprite(walkSheetPath, 192, 192);
            right9 = getWalkSprite(walkSheetPath, 256, 192);
            
            // Load run animations using exact offsets and 64x64 size
            // Up (row 1)
            BufferedImage runUp[] = new BufferedImage[8];
            for (int i = 0; i < 8; i++) {
                runUp[i] = getRunSprite(runSheetPath, i * 64, 0);
            }
            // Left (row 2)
            BufferedImage runLeft[] = new BufferedImage[8];
            for (int i = 0; i < 8; i++) {
                runLeft[i] = getRunSprite(runSheetPath, i * 64, 64);
            }
            // Down (row 3)
            BufferedImage runDown[] = new BufferedImage[8];
            for (int i = 0; i < 8; i++) {
                runDown[i] = getRunSprite(runSheetPath, i * 64, 128);
            }
            // Right (row 4)
            BufferedImage runRight[] = new BufferedImage[8];
            for (int i = 0; i < 8; i++) {
                runRight[i] = getRunSprite(runSheetPath, i * 64, 192);
            }
            // Assign to class fields for run animation (run5-run12)
            up10 = runUp[0]; up11 = runUp[1]; up12 = runUp[2]; up13 = runUp[3]; up14 = runUp[4]; up15 = runUp[5]; up16 = runUp[6]; up17 = runUp[7];
            left10 = runLeft[0]; left11 = runLeft[1]; left12 = runLeft[2]; left13 = runLeft[3]; left14 = runLeft[4]; left15 = runLeft[5]; left16 = runLeft[6]; left17 = runLeft[7];
            down10 = runDown[0]; down11 = runDown[1]; down12 = runDown[2]; down13 = runDown[3]; down14 = runDown[4]; down15 = runDown[5]; down16 = runDown[6]; down17 = runDown[7];
            right10 = runRight[0]; right11 = runRight[1]; right12 = runRight[2]; right13 = runRight[3]; right14 = runRight[4]; right15 = runRight[5]; right16 = runRight[6]; right17 = runRight[7];

            // Load slash sheets if withSword
            if (baseDir.contains("withSword")) {
                // slash3.png: 6x4
                slashSheets[0] = loadSlashSheet(baseDir + "slash3.png", 6, 4);
            } else {
                slashSheets[0] = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BufferedImage[][] loadSlashSheet(String path, int rows, int cols) {
        try {
            File file = new File(path);
            BufferedImage sheet = ImageIO.read(file);
            if (sheet == null) {
                return null;
            }
            
            BufferedImage[][] frames = new BufferedImage[rows][cols];
            
            // Common frame dimensions for all slash animations
            int frameW = 128, frameH = 128;
            
            // Calculate total expected dimensions
            int expectedWidth = cols * frameW;
            int expectedHeight = rows * frameH;
            
            // Check if sheet is wide enough for all columns
            if (sheet.getWidth() < expectedWidth) {
                return null;
            }
            
            // Check if sheet is tall enough for all rows
            if (sheet.getHeight() < expectedHeight) {
                return null;
            }
            
            // Load frames
            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < cols; x++) {
                    try {
                        int left = x * frameW;
                        int top = y * frameH;
                        frames[y][x] = sheet.getSubimage(left, top, frameW, frameH);
                    } catch (Exception e) {
                        return null;
                    }
                }
            }
            
            return frames;
        } catch (Exception e) {
            return null;
        }
    }

    public void setSwordTextures(boolean hasSword) {
        if (hasSword) {
            getPlayerImageFromDir("res/player/withSword/");
            // Ensure slash animations are loaded
            try {
                // Try loading with absolute paths
                String basePath = new File("").getAbsolutePath();
                String slash3Path = basePath + "/res/player/withSword/slash3.png";
                // slash3.png is 768x512, with 6 columns and 4 rows
                slashSheets[0] = loadSlashSheet(slash3Path, 6, 4);
            } catch (Exception e) {
                // Handle error silently
            }
        } else {
            getPlayerImageFromDir("res/player/");
            // Clear slash animation
            slashSheets[0] = null;
        }
    }
    
    // Updates player position and animation state based on input
    public void update() {
        // Handle inventory toggle/close
        if (keyH.iPressed) {
            inventory.toggle();
            keyH.iPressed = false;
        }

        // Don't process movement if inventory is open
        if (inventory.isOpen()) {
            return;
        }

        // Always increment spriteCounter
        spriteCounter++;
        // Don't process movement if in dialogue
        if (gp.gameState == GamePanel.DIALOGUE_STATE) {
            return;
        }

        // Clear last collision tiles before movement
        lastCollisionTiles.clear();

        boolean isMoving = keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed;
        boolean isRunning = keyH.shiftPressed && isMoving;
        
        // Track previous animation state
        String prevAnimationState = animationState;
        // Update animation state
        if (!isMoving) {
            animationState = "idle";
            if (spriteCounter > 12) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        } else if (isRunning && stamina > 0) {
            animationState = "run";
            if (!prevAnimationState.equals("run")) {
                spriteNum = 10; // Start at first run frame
            }
            if (spriteCounter > 12) {
                spriteNum = (spriteNum >= 17) ? 10 : spriteNum + 1;
                spriteCounter = 0;
            }
        } else {
            animationState = "walk";
            if (!prevAnimationState.equals("walk")) {
                spriteNum = 5; // Start at first walk frame
            }
            if (spriteCounter > 12) {
                spriteNum = (spriteNum >= 9) ? 5 : spriteNum + 1;
                spriteCounter = 0;
            }
        }
        
        // Only update if movement keys are pressed
        if (isMoving) {
            // Set direction based on key press
            if (keyH.upPressed) {
                direction = "up";
            } else if (keyH.downPressed) {
                direction = "down";
            } else if (keyH.leftPressed) {
                direction = "left";
            } else if (keyH.rightPressed) {
                direction = "right";
            }
            // Only allow running if stamina is above 0
            int moveSpeed = (isRunning && stamina > 0) ? speed * 2 : speed;
            for (int i = 0; i < moveSpeed; i++) {
                int prevX = worldX;
                int prevY = worldY;
                switch (direction) {
                    case "up" -> worldY--;
                    case "down" -> worldY++;
                    case "left" -> worldX--;
                    case "right" -> worldX++;
                }
                collisionOn = false;
                gp.cCheck.checkTile(this);
                gp.cCheck.checkEntity(this, gp.npc);
                if (collisionOn) {
                    worldX = prevX;
                    worldY = prevY;
                    break;
                }
            }
        }
        
        // Update stamina
        if (isRunning && stamina > 0) {
            gp.hud.drainStamina();
        } else {
            gp.hud.regenerateStamina(isMoving);
        }
        stamina = gp.hud.getStamina();
        
        // Update HUD
        String weaponName = weapon != null ? weapon.getName() : "No Weapon";
        gp.hud.update(hp, stamina, weaponName, isMoving);

        // Update slash animation
        if (isSlashing) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastFrameTime >= SLASH_FRAME_DURATION) {
                currentFrame++;
                lastFrameTime = currentTime;
                
                // Stop after playing through all frames once
                if (currentFrame >= 6) { // Only play through one row (6 frames)
                    isSlashing = false;
                    currentFrame = 0;
                }
            }
        }
    }

    public void draw(Graphics2D g2) {
        // Draw inventory overlay if open
        if (inventory.isOpen()) {
            g2.setColor(new java.awt.Color(0, 0, 0, 128));
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
            inventory.draw(g2);
            String weaponName = weapon != null ? weapon.getName() : "No Weapon";
            gp.hud.update(hp, stamina, weaponName, false);
            gp.hud.draw(g2, weapon);
            return;
        }

        BufferedImage image = null;
        int drawX = worldX - gp.player.worldX + gp.player.screenX;
        int drawY = worldY - gp.player.worldY + gp.player.screenY;

        // Draw slash animation if active
        if (isSlashing && slashSheets[0] != null) {
            int row = 0;
            switch (direction) {
                case "up": row = 0; break;
                case "left": row = 1; break;
                case "down": row = 2; break;
                case "right": row = 3; break;
            }
            int col = currentFrame % 6; // 6 columns in slash3.png
            image = slashSheets[0][row][col];
            if (image != null) {
                double scale = 2.6; // Double the scale for slash animations
                int drawWidth = (int)(gp.tileSize * scale);
                int drawHeight = (int)(gp.tileSize * scale);
                drawX = screenX - drawWidth / 2 + gp.tileSize / 2;
                drawY = screenY - drawHeight + gp.tileSize + gp.tileSize / 2 + 15; // Move slash animation down by 15 pixels total
                g2.drawImage(image, drawX, drawY, drawWidth, drawHeight, null);
            }
        } else {
            // Draw normal player sprite
            switch (direction) {
                case "up":
                    if (spriteNum == 1) image = up1;
                    if (spriteNum == 2) image = up2;
                    if (spriteNum == 3) image = up3;
                    if (spriteNum == 4) image = up4;
                    if (spriteNum == 5) image = up5;
                    if (spriteNum == 6) image = up6;
                    if (spriteNum == 7) image = up7;
                    if (spriteNum == 8) image = up8;
                    if (spriteNum == 9) image = up9;
                    if (spriteNum == 10) image = up10;
                    if (spriteNum == 11) image = up11;
                    if (spriteNum == 12) image = up12;
                    if (spriteNum == 13) image = up13;
                    if (spriteNum == 14) image = up14;
                    if (spriteNum == 15) image = up15;
                    if (spriteNum == 16) image = up16;
                    if (spriteNum == 17) image = up17;
                    break;
                case "down":
                    if (spriteNum == 1) image = down1;
                    if (spriteNum == 2) image = down2;
                    if (spriteNum == 3) image = down3;
                    if (spriteNum == 4) image = down4;
                    if (spriteNum == 5) image = down5;
                    if (spriteNum == 6) image = down6;
                    if (spriteNum == 7) image = down7;
                    if (spriteNum == 8) image = down8;
                    if (spriteNum == 9) image = down9;
                    if (spriteNum == 10) image = down10;
                    if (spriteNum == 11) image = down11;
                    if (spriteNum == 12) image = down12;
                    if (spriteNum == 13) image = down13;
                    if (spriteNum == 14) image = down14;
                    if (spriteNum == 15) image = down15;
                    if (spriteNum == 16) image = down16;
                    if (spriteNum == 17) image = down17;
                    break;
                case "left":
                    if (spriteNum == 1) image = left1;
                    if (spriteNum == 2) image = left2;
                    if (spriteNum == 3) image = left3;
                    if (spriteNum == 4) image = left4;
                    if (spriteNum == 5) image = left5;
                    if (spriteNum == 6) image = left6;
                    if (spriteNum == 7) image = left7;
                    if (spriteNum == 8) image = left8;
                    if (spriteNum == 9) image = left9;
                    if (spriteNum == 10) image = left10;
                    if (spriteNum == 11) image = left11;
                    if (spriteNum == 12) image = left12;
                    if (spriteNum == 13) image = left13;
                    if (spriteNum == 14) image = left14;
                    if (spriteNum == 15) image = left15;
                    if (spriteNum == 16) image = left16;
                    if (spriteNum == 17) image = left17;
                    break;
                case "right":
                    if (spriteNum == 1) image = right1;
                    if (spriteNum == 2) image = right2;
                    if (spriteNum == 3) image = right3;
                    if (spriteNum == 4) image = right4;
                    if (spriteNum == 5) image = right5;
                    if (spriteNum == 6) image = right6;
                    if (spriteNum == 7) image = right7;
                    if (spriteNum == 8) image = right8;
                    if (spriteNum == 9) image = right9;
                    if (spriteNum == 10) image = right10;
                    if (spriteNum == 11) image = right11;
                    if (spriteNum == 12) image = right12;
                    if (spriteNum == 13) image = right13;
                    if (spriteNum == 14) image = right14;
                    if (spriteNum == 15) image = right15;
                    if (spriteNum == 16) image = right16;
                    if (spriteNum == 17) image = right17;
                    break;
            }
            double scale = 1.3; // Normal scale for player
            int drawWidth = (int)(gp.tileSize * scale);
            int drawHeight = (int)(gp.tileSize * scale);
            drawX = screenX - drawWidth / 2 + gp.tileSize / 2;
            drawY = screenY - drawHeight + gp.tileSize;
            g2.drawImage(image, drawX, drawY, drawWidth, drawHeight, null);
        }
    }

    public void triggerSlash() {
        isSlashing = true;
        currentFrame = 0;
        lastFrameTime = System.currentTimeMillis();
        lastSlashTime = System.currentTimeMillis();
    }

    public void handleSpacePressed() {
        if (!isSlashing && System.currentTimeMillis() - lastSlashTime >= SLASH_COOLDOWN) {
            triggerSlash();
        }
    }

    public void drawAt(Graphics2D g2, int x, int y) {
        // Ensure sword textures are used if player has a sword
        if (weapon != null && (weapon.getName().toLowerCase().contains("sword") || weapon.getType().equalsIgnoreCase("sword"))) {
            setSwordTextures(true);
        } else {
            setSwordTextures(false);
        }
        BufferedImage image = null;
        int drawX = x;
        int drawY = y;
        int screenX = x;
        int screenY = y;
        // Draw slash animation if active
        if (isSlashing && slashSheets[0] != null) {
            int row = 0;
            switch (direction) {
                case "up": row = 0; break;
                case "left": row = 1; break;
                case "down": row = 2; break;
                case "right": row = 3; break;
            }
            int col = currentFrame % 6; // 6 columns in slash3.png
            image = slashSheets[0][row][col];
            if (image != null) {
                double scale = 2.6; // Double the scale for slash animations
                int drawWidth = (int)(gp.tileSize * scale);
                int drawHeight = (int)(gp.tileSize * scale);
                drawX = screenX - drawWidth / 2 + gp.tileSize / 2;
                drawY = screenY - drawHeight + gp.tileSize + gp.tileSize / 2 + 15; // Move slash animation down by 15 pixels total
                g2.drawImage(image, drawX, drawY, drawWidth, drawHeight, null);
                return;
            }
        }
        // Otherwise, draw normal sprite
        switch (direction) {
            case "up":
                if (spriteNum == 1) image = up1;
                if (spriteNum == 2) image = up2;
                if (spriteNum == 3) image = up3;
                if (spriteNum == 4) image = up4;
                if (spriteNum == 5) image = up5;
                if (spriteNum == 6) image = up6;
                if (spriteNum == 7) image = up7;
                if (spriteNum == 8) image = up8;
                if (spriteNum == 9) image = up9;
                if (spriteNum == 10) image = up10;
                if (spriteNum == 11) image = up11;
                if (spriteNum == 12) image = up12;
                if (spriteNum == 13) image = up13;
                if (spriteNum == 14) image = up14;
                if (spriteNum == 15) image = up15;
                if (spriteNum == 16) image = up16;
                if (spriteNum == 17) image = up17;
                if (image == null) image = up1;
                break;
            case "left":
                if (spriteNum == 1) image = left1;
                if (spriteNum == 2) image = left2;
                if (spriteNum == 3) image = left3;
                if (spriteNum == 4) image = left4;
                if (spriteNum == 5) image = left5;
                if (spriteNum == 6) image = left6;
                if (spriteNum == 7) image = left7;
                if (spriteNum == 8) image = left8;
                if (spriteNum == 9) image = left9;
                if (spriteNum == 10) image = left10;
                if (spriteNum == 11) image = left11;
                if (spriteNum == 12) image = left12;
                if (spriteNum == 13) image = left13;
                if (spriteNum == 14) image = left14;
                if (spriteNum == 15) image = left15;
                if (spriteNum == 16) image = left16;
                if (spriteNum == 17) image = left17;
                if (image == null) image = left1;
                break;
            case "down":
                if (spriteNum == 1) image = down1;
                if (spriteNum == 2) image = down2;
                if (spriteNum == 3) image = down3;
                if (spriteNum == 4) image = down4;
                if (spriteNum == 5) image = down5;
                if (spriteNum == 6) image = down6;
                if (spriteNum == 7) image = down7;
                if (spriteNum == 8) image = down8;
                if (spriteNum == 9) image = down9;
                if (spriteNum == 10) image = down10;
                if (spriteNum == 11) image = down11;
                if (spriteNum == 12) image = down12;
                if (spriteNum == 13) image = down13;
                if (spriteNum == 14) image = down14;
                if (spriteNum == 15) image = down15;
                if (spriteNum == 16) image = down16;
                if (spriteNum == 17) image = down17;
                if (image == null) image = down1;
                break;
            case "right":
                if (spriteNum == 1) image = right1;
                if (spriteNum == 2) image = right2;
                if (spriteNum == 3) image = right3;
                if (spriteNum == 4) image = right4;
                if (spriteNum == 5) image = right5;
                if (spriteNum == 6) image = right6;
                if (spriteNum == 7) image = right7;
                if (spriteNum == 8) image = right8;
                if (spriteNum == 9) image = right9;
                if (spriteNum == 10) image = right10;
                if (spriteNum == 11) image = right11;
                if (spriteNum == 12) image = right12;
                if (spriteNum == 13) image = right13;
                if (spriteNum == 14) image = right14;
                if (spriteNum == 15) image = right15;
                if (spriteNum == 16) image = right16;
                if (spriteNum == 17) image = right17;
                if (image == null) image = right1;
                break;
        }
        double scale = 1.3;
        int drawWidth = (int)(gp.tileSize * scale);
        int drawHeight = (int)(gp.tileSize * scale);
        drawX = screenX - drawWidth / 2 + gp.tileSize / 2;
        drawY = screenY - drawHeight + gp.tileSize;
        g2.drawImage(image, drawX, drawY, drawWidth, drawHeight, null);
    }
}
