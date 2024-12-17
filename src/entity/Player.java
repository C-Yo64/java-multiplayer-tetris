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

    //    int speed = 4;
    //    static String currentBlock;
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
        gp.menu.winner = "";
        size = gp.tileSize;
        posX = 5 * gp.tileSize;
        posY = 0 * gp.tileSize;
        score = 0;

        squares = new ArrayList<>();
        placedBlocks = new ArrayList<>();
        rotationState = 0;
        allBlocks = new String[]{"line", "square", "T", "L", "reverse L", "Z", "S"};

        randomizeBlock();
        moveTimer = new Timer();
//        moveTimer.scheduleAtFixedRate(() -> fallingBlock(), 1000, 1000);
        moveTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                fallingBlock();
            }
        }, 1000, 1000);

        ghostSquares = new ArrayList<>();
        ghostBlock();
        gp.playMusic(0);
        
//        PlacedBlock placed = new PlacedBlock(-999, -999, size, squares);
//        placedBlocks.add(placed);
    }

    private void getPlayerImage() {
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
        Random random = new Random();
        currentBlock = allBlocks[random.nextInt(0, 7)];
        rotationState = 0;
        posX = 5 * gp.tileSize;
        posY = 0;
        createBlock(currentBlock, rotationState);
    }

    public void createBlock(String blockType, int rotationState) {
        squares.clear();
        switch (blockType) {
            case "line":
                switch (rotationState) {
                    case 0:
                        squares.add(new Point(0, 0));
                        squares.add(new Point(0, size));
                        squares.add(new Point(0, 2 * size));
                        squares.add(new Point(0, 3 * size));
                        break;
                    case 1:
                        squares.add(new Point(0, 0));
                        squares.add(new Point(size, 0));
                        squares.add(new Point(2 * size, 0));
                        squares.add(new Point(3 * size, 0));
                }
                numberOfRotationStates = 2;
                break;

            case "square":
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
        int newY = posY;

        newY += size;
        // Check for collision before updating position
        if (!collisionHandler.isColliding(posX, newY, size, squares, placedBlocks)) {
            posY = newY;
        } else {
            PlacedBlock placed = new PlacedBlock(posX, posY, squares, currentBlock);
            placedBlocks.add(placed);
            gp.playSE(2);

            lineClear();

            randomizeBlock();
            if (collisionHandler.isColliding(posX, posY, size, squares, placedBlocks)) {
                gameOver();
            }
        }
        ghostBlock();
    }

    public void dropBlock() {
        Point ghostSquare = ghostSquares.getFirst();
        Point thisSquare = squares.getFirst();
        posY += (ghostSquare.y - thisSquare.y);
        fallingBlock();
    }

    public void update() {
    }

    public void moveBlock(String direction) {
        int newX = posX;
        int newY = posY;

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

        // Check for collision before updating position
        if (!collisionHandler.isColliding(newX, newY, size, squares, placedBlocks)) {
            posX = newX;
            posY = newY;
            ghostBlock();
        }
    }

    public void rotateBlock() {
        List<Point> originalSquares = new ArrayList<>();
        originalSquares.addAll(squares);
        rotationState = (rotationState + 1) % numberOfRotationStates;
        createBlock(currentBlock, rotationState);

        // Perform collision detection with the rotated positions
        if (collisionHandler.isColliding(posX, posY, size, squares, placedBlocks)) {
            squares.clear();
            squares.addAll(originalSquares);
            rotationState = (rotationState - 1) % numberOfRotationStates;
        } else {
            ghostBlock();
            gp.playSE(1);
        }
    }

    public void ghostBlock() {
        try {
            ghostSquares.clear();

            for (Point square : squares) {
                // addAll doesn't work here because it (for some reason) makes it so that all changes to this array are also applied to squares
                ghostSquares.add(new Point(square.x, square.y));
            }

            while (!collisionHandler.isColliding(posX, posY, size, ghostSquares, placedBlocks)) {
                for (Point ghostSquare : ghostSquares) {
                    ghostSquare.y += size;
                }
            }
            for (Point ghostSquare : ghostSquares) {
                ghostSquare.y -= size;
            }
        } catch (Exception e) {

        }
    }

    public void lineClear() {
        List<Point> lineCheck = new ArrayList<>();
        int lineY = 0;
        int linesCleared = 0;
        for (Point square : squares) {
            int blocksColliding = 0;
            for (int i = 1; i <= 10; i++) {
//                System.out.println(i);
                lineCheck.clear();
                lineCheck.add(new Point(i * size - size, square.y));

                if (collisionHandler.isColliding(size, posY, size, lineCheck, placedBlocks)) {
                    blocksColliding++;
                }
            }
//            System.out.println(blocksColliding);
            if (blocksColliding == 10) {
//                System.out.println("line cleared");
                linesCleared++;
                score += 50;
                lineY = square.y + posY;
                for (PlacedBlock placedBlock : placedBlocks) {
                    Iterator<Point> iterator = placedBlock.squares.iterator();
                    while (iterator.hasNext()) {
                        Point placedSquare = iterator.next();
                        if (placedSquare.y + placedBlock.posY == lineY) {
//                            System.out.println("square removed");
                            iterator.remove();
                        }
                    }
                }
            }
        }

        if (linesCleared > 0) {
            gp.playSE(3);

            if (network.out != null) {
                network.sendMessage(network.out, String.valueOf(linesCleared));
            }

            Timer timer = new Timer();
            int finalLineY = lineY; // The IDE told me to do this and when I didn't do it there was an error
            int finalLinesCleared = linesCleared;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    for (PlacedBlock placedBlock : placedBlocks) {
                        for (Point placedSquare : placedBlock.squares) {
                            if (placedSquare.y + placedBlock.posY < finalLineY) {
//                                System.out.println("square shifted");
                                placedSquare.y += size * finalLinesCleared;
                                ghostBlock();
                            }
                        }
                    }
                }
            }, 500);
        }
    }

    public void grayLines(int lines) {
        for (PlacedBlock placedBlock : placedBlocks) {
            for (Point placedSquare : placedBlock.squares) {
                placedSquare.y -= size * lines;
            }
        }

        for (int i_ = 1; i_ <= lines; i_++) {
            List<Point> grayBlocks = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                grayBlocks.add(new Point(i * size, (22 * size) - size - (i_ * size)));
            }
            Random random = new Random();
            int emptyBlock = random.nextInt(1, 11);
            grayBlocks.removeIf(grayBlock -> grayBlock.x == emptyBlock * size);

            PlacedBlock placed = new PlacedBlock(0, 0, grayBlocks, "gray");
            placedBlocks.add(placed);
        }

        ghostBlock();
    }

//    List<Point> grayBlocksAnim = new ArrayList<>();

    public void gameOver() {
        if (network.connected) {
            network.sendMessage(network.out, "game over");
            gp.menu.gameOver(true);
            remove();
//            grayBlocksAnimation(gp.grayBlocksAnim, false);
        } else {
            grayBlocksAnimation(gp.grayBlocksAnim, true);

            for (PlacedBlock placedBlock : placedBlocks) {
                Iterator<Point> iterator = placedBlock.squares.iterator();
                while (iterator.hasNext()) {
                    Point placedSquare = iterator.next();
                    iterator.remove();

                }
            }
            score = 0;
            randomizeBlock();
        }
    }

    public void remove() {
        moveTimer.cancel();
        moveTimer = null;

        size = 0;
        posX = 0;
        posY = 0;
        rotationState = 0;
        score = 0;

        squares.clear();
        squares = null;
        ghostSquares.clear();
        ghostSquares = null;
        allBlocks = null;
        for (PlacedBlock placedBlock : placedBlocks) {
            Iterator<Point> iterator = placedBlock.squares.iterator();
            while (iterator.hasNext()) {
                Point placedSquare = iterator.next();
                iterator.remove();

            }
        }
        placedBlocks = null;

        gp.stopMusic();
    }

    public void grayBlocksAnimation(List<Point> grayBlocksAnim, boolean inGame) {
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
                            grayBlocksAnim.add(new Point(i * gp.tileSize, (finalI_ * gp.tileSize) - gp.tileSize));
                            gp.repaint();
                        }
                    }
                }
            }, 20 * i_);
        }
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
//                                grayBlocksAnim.add(new Point(i * gp.tileSize, (finalI_ * gp.tileSize) - gp.tileSize));
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
                    }, 20 * i_);
                }
            }
        }, 700);
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.black);
        BufferedImage image;
//        g2.drawImage(bg, 0, 0, gp.screenWidth, gp.screenHeight, null);
        for (int i = 1; i <= 10; i++) {
            g2.drawLine(i * gp.tileSize, 0, i * gp.tileSize, gp.screenHeight);
        }
        for (int i = 1; i <= 21; i++) {
            g2.drawLine(0, i * gp.tileSize, gp.screenWidth, i * gp.tileSize);
        }

        for (Point ghostSquare : ghostSquares) {
            image = ghost;
            g2.drawImage(image, posX + ghostSquare.x, posY + ghostSquare.y, gp.tileSize, size, null);
        }

        for (Point square : squares) {
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
        for (PlacedBlock placedBlock : placedBlocks) {
            for (Point square : placedBlock.getAbsoluteSquares()) {
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

        g2.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fontMetrics = g2.getFontMetrics();
        g2.drawString(String.valueOf(score), size + size / 3, (size*2) - fontMetrics.getHeight()/2);

//        try {
//            for (Point square : gp.grayBlocksAnim) {
//                image = gray;
//                g2.drawImage(image, square.x, square.y, size, size, null);
//            }
//        } catch (Exception e) {
//
//        }
    }
}