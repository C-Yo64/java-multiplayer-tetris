package entity;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;

import main.CollisionHandler;
import main.GamePanel;
import main.Network;

import javax.imageio.ImageIO;

public class Player extends Entity {

    GamePanel gp;
    static CollisionHandler collisionHandler;
    Network network;

    static int rotationState;
    static int numberOfRotationStates;
    static String[] allBlocks;

    List<Point> ghostSquares;

    public BufferedImage spriteSheet;
    public BufferedImage yellow, red, green, lightblue, darkblue, orange, purple, gray, ghost;
    int spriteNum = 1;
    int spriteCounter = 0;

    private static List<PlacedBlock> placedBlocks;

    public int score;

    public Timer moveTimer;

    // Constructor
    public Player(GamePanel gamepanel, CollisionHandler collisionHandler, Network network) {
        this.gp = gamepanel;
        this.collisionHandler = collisionHandler;
        this.network = network;

        getPlayerImage();

        // This is to prevent it from lagging the first time a sound is played
        gp.playMusic(0);
        gp.stopMusic();
    }

    public void initialize() {
        // Sets up everything needed to start playing the game

        // No one has won the game yet, so if someone disconnects it won't show the previous game's winner
        gp.menu.winner = "";

        // Set the size and starting position
        size = gp.tileSize;
        posX = 5 * gp.tileSize;
        posY = 0 * gp.tileSize;
        score = 0;

        // Create the arrays that the current block and the placed blocks will be stored in
        squares = new ArrayList<>();
        placedBlocks = new ArrayList<>();
        rotationState = 0;

        // Create an array of all the block types so that they can be randomly selected
        allBlocks = new String[]{"line", "square", "T", "L", "reverse L", "Z", "S"};
        // Randomly select a block and add the squares from it to the array
        randomizeBlock();

        // Start the timer that ticks every second and makes the blocks "fall"/place down
        moveTimer = new Timer();
        moveTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                fallingBlock();
            }
        }, 1000, 1000);

        // Set up the ghost square visual to help the player know where the block will land
        ghostSquares = new ArrayList<>();
        ghostBlock();

        // Play the tetris music
        gp.playMusic(0);

    }

    private void getPlayerImage() {
        // Iterate through the squares in the spritesheet and assign them to the different block types

        int frameWidth = 300;
        int frameHeight = 300;

        try {
            spriteSheet = ImageIO.read(getClass().getResourceAsStream("/sprites/block_spritesheet.png"));

            lightblue = spriteSheet.getSubimage(0, 0, frameWidth, frameHeight);
            purple = spriteSheet.getSubimage(frameWidth, 0, frameWidth, frameHeight);
            green = spriteSheet.getSubimage(2 * frameWidth, 0, frameWidth, frameHeight);
            yellow = spriteSheet.getSubimage(3 * frameWidth, 0, frameWidth, frameHeight);
            darkblue = spriteSheet.getSubimage(4 * frameWidth, 0, frameWidth, frameHeight);
            red = spriteSheet.getSubimage(5 * frameWidth, 0, frameWidth, frameHeight);
            orange = spriteSheet.getSubimage(6 * frameWidth, 0, frameWidth, frameHeight);
            gray = spriteSheet.getSubimage(7 * frameWidth, 0, frameWidth, frameHeight);
            ghost = spriteSheet.getSubimage(8 * frameWidth, 0, frameWidth, frameHeight);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void randomizeBlock() {
        // Randomly select a block type from the list
        Random random = new Random();
        currentBlock = allBlocks[random.nextInt(0, 7)];

        // Reset the position and create the block that was picked
        rotationState = 0;
        posX = 5 * gp.tileSize;
        posY = 0;
        createBlock(currentBlock, rotationState);
    }

    public void createBlock(String blockType, int rotationState) {
        // Clear the previous block and set each of the 4 squares that make up the block to their local positions according to the block type
        // The different ways the block can be rotated are accounted for, and a new block will be created at the current position with the new values when the player rotates the block
        // Making each block an array of 4 tiles makes it easier to do calculations since I can treat each square as its own tile
        squares.clear();
        switch (blockType) {
            case "line":
                switch (rotationState) {
                    case 0:
                        // vertical line
                        squares.add(new Point(0, 0));
                        squares.add(new Point(0, size));
                        squares.add(new Point(0, 2 * size));
                        squares.add(new Point(0, 3 * size));
                        break;
                    case 1:
                        // horizontal line
                        squares.add(new Point(0, 0));
                        squares.add(new Point(size, 0));
                        squares.add(new Point(2 * size, 0));
                        squares.add(new Point(3 * size, 0));
                }
                numberOfRotationStates = 2;
                break;

            case "square":
                // the square can't be rotated and has only 1 rotation state
                squares.add(new Point(0, 0));
                squares.add(new Point(size, 0));
                squares.add(new Point(0, size));
                squares.add(new Point(size, size));
                numberOfRotationStates = 1;
                break;

            case "T":
                switch (rotationState) { // 4 rotation states: 0,1,2,3
                    case 0:
                        // T pointing up
                        squares.add(new Point(size, 0));
                        squares.add(new Point(0, size));
                        squares.add(new Point(size, size));
                        squares.add(new Point(2 * size, size));
                        break;
                    case 1:
                        // T pointing right
                        squares.add(new Point(size, 0));
                        squares.add(new Point(size, size));
                        squares.add(new Point(size, 2 * size));
                        squares.add(new Point(2 * size, size));
                        break;
                    case 2:
                        // T pointing down
                        squares.add(new Point(0, size));
                        squares.add(new Point(size, size));
                        squares.add(new Point(2 * size, size));
                        squares.add(new Point(size, 2 * size));
                        break;
                    case 3:
                        // T pointing left
                        squares.add(new Point(0, size));
                        squares.add(new Point(size, 0));
                        squares.add(new Point(size, size));
                        squares.add(new Point(size, 2 * size));
                        break;
                }
                numberOfRotationStates = 4;
                break;

            case "L":
                switch (rotationState) { // 4 rotation states: 0,1,2,3
                    case 0:
                        // L pointing down
                        squares.add(new Point(0, 0));
                        squares.add(new Point(0, size));
                        squares.add(new Point(0, 2 * size));
                        squares.add(new Point(size, 2 * size));
                        break;
                    case 1:
                        // L pointing right
                        squares.add(new Point(0, 0));
                        squares.add(new Point(size, 0));
                        squares.add(new Point(2 * size, 0));
                        squares.add(new Point(0, size));
                        break;
                    case 2:
                        // L pointing up
                        squares.add(new Point(0, 0));
                        squares.add(new Point(size, 0));
                        squares.add(new Point(size, size));
                        squares.add(new Point(size, 2 * size));
                        break;
                    case 3:
                        // L pointing left
                        squares.add(new Point(2 * size, 0));
                        squares.add(new Point(0, size));
                        squares.add(new Point(size, size));
                        squares.add(new Point(2 * size, size));
                        break;
                }
                numberOfRotationStates = 4;
                break;

            case "reverse L":
                switch (rotationState) { // 4 rotation states: 0,1,2,3
                    case 0:
                        // reverse L pointing down
                        squares.add(new Point(size, 0));
                        squares.add(new Point(size, size));
                        squares.add(new Point(0, 2 * size));
                        squares.add(new Point(size, 2 * size));
                        break;
                    case 1:
                        // reverse L pointing right
                        squares.add(new Point(0, 0));
                        squares.add(new Point(size, size));
                        squares.add(new Point(2 * size, size));
                        squares.add(new Point(0, size));
                        break;
                    case 2:
                        // reverse L pointing up
                        squares.add(new Point(0, 0));
                        squares.add(new Point(size, 0));
                        squares.add(new Point(0, size));
                        squares.add(new Point(0, 2 * size));
                        break;
                    case 3:
                        // reverse L pointing left
                        squares.add(new Point(2 * size, 0));
                        squares.add(new Point(0, 0));
                        squares.add(new Point(size, 0));
                        squares.add(new Point(2 * size, size));
                        break;
                }
                numberOfRotationStates = 4;
                break;

            case "Z":
                switch (rotationState % 2) { // 2 rotation states: 0 and 1
                    case 0:
                        // Z pointing right
                        squares.add(new Point(0, 0));
                        squares.add(new Point(size, 0));
                        squares.add(new Point(size, size));
                        squares.add(new Point(2 * size, size));
                        break;
                    case 1:
                        // Z pointing down
                        squares.add(new Point(size, 0));
                        squares.add(new Point(size, size));
                        squares.add(new Point(0, size));
                        squares.add(new Point(0, 2 * size));
                        break;
                }
                numberOfRotationStates = 2;
                break;

            case "S":
                switch (rotationState % 2) { // 2 rotation states: 0 and 1
                    case 0:
                        // S pointing left
                        squares.add(new Point(size, 0));
                        squares.add(new Point(2 * size, 0));
                        squares.add(new Point(0, size));
                        squares.add(new Point(size, size));
                        break;
                    case 1:
                        // S pointing down
                        squares.add(new Point(0, 0));
                        squares.add(new Point(0, size));
                        squares.add(new Point(size, size));
                        squares.add(new Point(size, 2 * size));
                        break;
                }
                numberOfRotationStates = 2;
                break;
        }
    }

    public void fallingBlock() {
        // Moves the block down by 1 tile and if it collides with something, place the block

        // Store the potential next position separately so it can be reverted if it would collide
        int newY = posY;
        newY += size;

        // Check for collision before updating the block position
        if (!collisionHandler.isColliding(posX, newY, size, squares, placedBlocks)) {
            // If it doesn't collide, apply the new position
            posY = newY;
        } else {
            // If it does collide, place the block and make a new one

            // Add the squares from the block to a new block
            PlacedBlock placed = new PlacedBlock(posX, posY, squares, currentBlock);
            // Put the new block in the placedBlocks array (used later for collision)
            placedBlocks.add(placed);

            // Play the block landing sound effect
            gp.playSE(2);

            // Since a new block has been placed, check if any lines were completed and clear them
            lineClear();

            // Pick a new block for the player and bring it to the top of the screen
            randomizeBlock();
            // If the new block collides with anything as soon as it was created, trigger a game over
            if (collisionHandler.isColliding(posX, posY, size, squares, placedBlocks)) {
                gameOver();
            }
        }

        // Since the block's position has changed, update where ghost block visualization
        ghostBlock();
    }

    public void dropBlock() {
        // Set the position of the block to the position of the ghost block (since the ghost block is as far down as the block can go before colliding) and place it

        // Get the difference between the position of the first square of the blocks and add it to the player position
        Point ghostSquare = ghostSquares.getFirst();
        Point thisSquare = squares.getFirst();
        posY += (ghostSquare.y - thisSquare.y);

        // Place the block
        fallingBlock();
    }

    public void update() {
    }

    public void moveBlock(String direction) {
        // This is called when a direction key is pressed
        // Moves the block over by 1 tile in the direction pressed, and checks for collision before applying the move

        // Store the potential next position separately so it can be reverted if it would collide
        int newX = posX;
        int newY = posY;

        // Move the block over based on the direction pressed
        switch (direction) {
            case "left":
                newX -= size;
                break;
            case "right":
                newX += size;
                break;
            case "up":
                newY -= size;
                break;
            case "down":
                newY += size;
                break;
        }

        // Check for collision before updating the block position
        if (!collisionHandler.isColliding(newX, newY, size, squares, placedBlocks)) {
            // If it doesn't collide, apply the new position
            posX = newX;
            posY = newY;
            ghostBlock();
        }
        // If it does collide, the function ends and nothing happens
    }

    public void rotateBlock() {
        // This is called when the rotate key is pressed
        // Changes the square positions in the block to the next one in the createBlock function, and checks for collision before applying the rotation

        // Store an array with the original block in case it needs to be reverted
        List<Point> originalSquares = new ArrayList<>();
        originalSquares.addAll(squares);

        // Change the block to the next case for the rotationState switch statement
        rotationState = (rotationState + 1) % numberOfRotationStates;
        createBlock(currentBlock, rotationState);

        // Check for collision with the rotated block
        if (collisionHandler.isColliding(posX, posY, size, squares, placedBlocks)) {
            // If it collides, restore the original block from the array
            squares.clear();
            squares.addAll(originalSquares);
            // Change the rotation state back because it couldn't successfully rotate
            rotationState = (rotationState - 1) % numberOfRotationStates;
        } else {
            // If it doesn't collide, update the ghost block visual and play a sound effect
            ghostBlock();
            gp.playSE(1);
        }
    }

    public void ghostBlock() {
        // Takes the player block and makes a visual-only copy with a different sprite that is moved down as far as it can go before it collides

        try {
            // Clear any previous ghost squares and add the squares from the player block
            ghostSquares.clear();
            for (Point square : squares) {
                // addAll doesn't work here because it for some reason makes it so that all changes to this array are also applied to the original squares array
                ghostSquares.add(new Point(square.x, square.y));
            }

            // Move each square in the array down by 1 tile repeatedly until a square collides
            while (!collisionHandler.isColliding(posX, posY, size, ghostSquares, placedBlocks)) {
                for (Point ghostSquare : ghostSquares) {
                    ghostSquare.y += size;
                }
            }
            // Since the ghost squares are now colliding, move them up by 1 tile so they no longer collide
            for (Point ghostSquare : ghostSquares) {
                ghostSquare.y -= size;
            }
        } catch (Exception e) {
            // Sometimes this threw an error but still worked anyway so I just catch the error
        }
    }

    public void lineClear() {
        // Iterates through each line to see if there are squares filling it up, and if so, clears that lines and shifts everything above it down

        // Create the list of points that get checked for squares
        List<Point> lineCheck = new ArrayList<>();
        int lineY = 0;
        int linesCleared = 0;
        // For each y position in the block, create an array of squares the length of a line and count how many blocks it collides with
        for (Point square : squares) {
            int blocksColliding = 0;
            for (int i = 1; i <= 10; i++) {
                // Add a point to the lineCheck that has the y position of the square from the first for loop and the x position from the second for loop
                lineCheck.clear();
                lineCheck.add(new Point(i * size - size, square.y));

                if (collisionHandler.isColliding(size, posY, size, lineCheck, placedBlocks)) {
                    blocksColliding++;
                }
            }

            if (blocksColliding == 10) {
                // If all 10 squares in the line are present, clear the line

                // Add to the score and count how many lines were cleared with this block placement
                linesCleared++;
                score += 50;

                // Remove each square in the line
                lineY = square.y + posY;
                for (PlacedBlock placedBlock : placedBlocks) {
                    // For each placed block, iterate through each square to see if it matches the y position of the line
                    Iterator<Point> iterator = placedBlock.squares.iterator();
                    while (iterator.hasNext()) {
                        Point placedSquare = iterator.next();
                        if (placedSquare.y + placedBlock.posY == lineY) {
                            // If the y positions match, remove the square
                            iterator.remove();
                        }
                    }
                }
            }
        }

        if (linesCleared > 0) {
            // If 1 or more lines were cleared, shift everything above them down, play a sound effect, and send a message to the other player (if there is one)

            // Play the line clear sound effect
            gp.playSE(3);

            if (network.out != null) {
                // If playing in multiplayer, send a message to the other player to create gray lines for them
                network.sendMessage(network.out, String.valueOf(linesCleared));
            }

            // Create a timer that, after half a second, shifts everything above the cleared lines down
            Timer timer = new Timer();
            int finalLineY = lineY; // The IDE told me to add this extra variable and when I didn't do it there was an error
            int finalLinesCleared = linesCleared;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    for (PlacedBlock placedBlock : placedBlocks) {
                        for (Point placedSquare : placedBlock.squares) {
                            // For each placed square, check if it's above the lines that were cleared
                            if (placedSquare.y + placedBlock.posY < finalLineY) {
                                // If it is, shift it down by the amount of lines cleared * the size of 1 tile
                                placedSquare.y += size * finalLinesCleared;

                                // Recalculate the ghost block since the placed blocks have changed
                                ghostBlock();
                            }
                        }
                    }
                }
            }, 500);
        }
    }

    public void grayLines(int lines) {
        // Add lines to the bottom of the screen that need to be cleared and shift everything up to account for them

        // Shift every placed square up by the amount of lines added * the size of 1 tile
        for (PlacedBlock placedBlock : placedBlocks) {
            for (Point placedSquare : placedBlock.squares) {
                placedSquare.y -= size * lines;
            }
        }

        for (int i_ = 1; i_ <= lines; i_++) {
            // For each line, create an array of all the squares in the line
            List<Point> grayBlocks = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                // Add a square for each tile in the line
                grayBlocks.add(new Point(i * size, (22 * size) - size - (i_ * size)));
            }

            // Randomly pick a block to remove from the line so that it's clearable
            Random random = new Random();
            int emptyBlock = random.nextInt(1, 11);
            grayBlocks.removeIf(grayBlock -> grayBlock.x == emptyBlock * size); // Remove the square that matches the randomly selected x position

            // Add the gray line as a placedBlock object
            PlacedBlock placed = new PlacedBlock(0, 0, grayBlocks, "gray");
            placedBlocks.add(placed);
        }

        // Recalculate the ghost block since the placed blocks have changed
        ghostBlock();
    }

    public void gameOver() {
        // Resets the game and if in multiplayer, goes to the main menu

        if (network.connected) {
            // If playing in multiplayer, send a message to the other player to indicate that you lost
            network.sendMessage(network.out, "game over");
            // Go to the game over menu
            gp.menu.gameOver(true);
            // Remove most data from the player object. Initialize will need to be called again to run the game
            remove();
        } else {
            // Play the gray blocks animation to fill the playable area with gray blocks while the game resets
            grayBlocksAnimation(gp.grayBlocksAnim, true);

            // Remove all the placed blocks
            for (PlacedBlock placedBlock : placedBlocks) {
                Iterator<Point> iterator = placedBlock.squares.iterator();
                while (iterator.hasNext()) {
                    Point placedSquare = iterator.next();
                    iterator.remove();

                }
            }

            // Reset the score and create a new block
            score = 0;
            randomizeBlock();
        }
    }

    public void remove() {
        // Removes most data used for the player and cancels the timer and music

        // Stop the timer and remove it
        moveTimer.cancel();
        moveTimer = null;

        // Reset position, size, and score integers
        size = 0;
        posX = 0;
        posY = 0;
        rotationState = 0;
        score = 0;

        // Remove the player block, the ghost block, and the array containing the types of blocks
        squares.clear();
        squares = null;
        ghostSquares.clear();
        ghostSquares = null;
        allBlocks = null;

        // Remove any placed blocks
        for (PlacedBlock placedBlock : placedBlocks) {
            Iterator<Point> iterator = placedBlock.squares.iterator();
            while (iterator.hasNext()) {
                Point placedSquare = iterator.next();
                iterator.remove();

            }
        }
        placedBlocks = null;

        // Stop playing the music
        gp.stopMusic();
    }

    public void grayBlocksAnimation(List<Point> grayBlocksAnim, boolean inGame) {
        // Plays an animation of gray blocks filling the screen (or just the play area if inGame is true), staying there for a bit, then disappearing

        // Set the values for where the blocks will fill based on the inGame parameter
        int base = 0;
        int height = 0;
        int width = 0;
        if (inGame) {
            base = 1;
            height = 21;
            width = 10;
        }
        if (!inGame) {
            base = 0;
            height = 22;
            width = 11;
        }

        // Every 20ms, add a line of gray blocks to the screen until the screen is covered
        for (int i_ = base; i_ <= height; i_++) {
            Timer timer = new Timer();
            int finalI_ = i_;
            int finalBase = base;
            int finalWidth = width;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    for (int i = finalBase; i <= finalWidth; i++) {
                        synchronized (grayBlocksAnim) {
                            // This array stores the gray squares. All the squares in a line are added at once, but each line is delayed by 20ms from the previous one
                            grayBlocksAnim.add(new Point(i * gp.tileSize, (finalI_ * gp.tileSize) - gp.tileSize));
                            gp.repaint();
                        }
                    }
                }
            }, 20 * i_); // Each line gets added 20ms after the one before it was added
        }

        // After 700ms, remove each line with the same timing they were added in
        Timer timer = new Timer();
        int finalBase1 = base;
        int finalHeight = height;
        int finalBase2 = base;
        int finalWidth1 = width;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (int i_ = finalBase1; i_ <= finalHeight; i_++) {
                    Timer timer = new Timer();
                    int finalI_ = i_;
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            for (int i = finalBase2; i <= finalWidth1; i++) {
                                // Remove each square that in the current line
                                Iterator<Point> iterator = grayBlocksAnim.iterator();
                                while (iterator.hasNext()) {
                                    Point grayBlock = iterator.next();
                                    if (grayBlock.x == i * gp.tileSize && grayBlock.y == (finalI_ * gp.tileSize) - gp.tileSize) {
                                        synchronized (grayBlocksAnim) {
                                            iterator.remove();
                                            gp.repaint();
                                        }
                                    }
                                }
                            }
                        }
                    }, 20 * i_); // Each line gets removed 20ms after the one before it was removed
                }
            }
        }, 700); // The blocks stay on the screen for 700ms before they start getting removed
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.black);
        BufferedImage image; // Image variable that gets assigned to each image to be drawn

        // Draw the lines for the grid
        for (int i = 1; i <= 10; i++) {
            g2.drawLine(i * gp.tileSize, 0, i * gp.tileSize, gp.screenHeight);
        }
        for (int i = 1; i <= 21; i++) {
            g2.drawLine(0, i * gp.tileSize, gp.screenWidth, i * gp.tileSize);
        }

        // Draw each square in the ghost block
        for (Point ghostSquare : ghostSquares) {
            image = ghost;
            g2.drawImage(image, posX + ghostSquare.x, posY + ghostSquare.y, gp.tileSize, size, null);
        }

        // Draw each square in the player's current block
        for (Point square : squares) {
            // Pick the colour of the block based on the block type
            switch (currentBlock) {
                case "line":
                    image = lightblue;
                    g2.drawImage(image, posX + square.x, posY + square.y, size, size, null);
                    break;

                case "square":
                    image = yellow;
                    g2.drawImage(image, posX + square.x, posY + square.y, size, size, null);
                    break;

                case "T":
                    image = purple;
                    g2.drawImage(image, posX + square.x, posY + square.y, size, size, null);
                    break;

                case "L":
                    image = orange;
                    g2.drawImage(image, posX + square.x, posY + square.y, size, size, null);
                    break;

                case "reverse L":
                    image = darkblue;
                    g2.drawImage(image, posX + square.x, posY + square.y, size, size, null);
                    break;

                case "Z":
                    image = red;
                    g2.drawImage(image, posX + square.x, posY + square.y, size, size, null);
                    break;

                case "S":
                    image = green;
                    g2.drawImage(image, posX + square.x, posY + square.y, size, size, null);
                    break;
            }
        }

        // Draw each square in the placed blocks
        for (PlacedBlock placedBlock : placedBlocks) {
            for (Point square : placedBlock.getAbsoluteSquares()) {
                // Pick the colour of the squares based on the original block's block type
                switch (placedBlock.currentBlock) {
                    case "line":
                        image = lightblue;
                        g2.drawImage(image, square.x, square.y, size, size, null);
                        break;

                    case "square":
                        image = yellow;
                        g2.drawImage(image, square.x, square.y, size, size, null);
                        break;

                    case "T":
                        image = purple;
                        g2.drawImage(image, square.x, square.y, size, size, null);
                        break;

                    case "L":
                        image = orange;
                        g2.drawImage(image, square.x, square.y, size, size, null);
                        break;

                    case "reverse L":
                        image = darkblue;
                        g2.drawImage(image, square.x, square.y, size, size, null);
                        break;

                    case "Z":
                        image = red;
                        g2.drawImage(image, square.x, square.y, size, size, null);
                        break;

                    case "S":
                        image = green;
                        g2.drawImage(image, square.x, square.y, size, size, null);
                        break;

                    case "gray":
                        image = gray;
                        g2.drawImage(image, square.x, square.y, size, size, null);
                        break;
                }
            }
        }

        // Draw the score
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fontMetrics = g2.getFontMetrics();
        g2.drawString(String.valueOf(score), size + size / 3, (size*2) - fontMetrics.getHeight()/2);

    }
}