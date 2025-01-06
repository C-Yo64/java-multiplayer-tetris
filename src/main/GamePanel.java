package main;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import entity.Player;
import tile.Tile;
import tile.TileManager;


public class GamePanel extends JPanel implements Runnable {

    private Thread thread;
    private final int FPS = 60;

    private final int originalTileSize = 16;
    private final int scale = 2;
    public final int tileSize = originalTileSize * scale;

    public final int screenWidth = tileSize * 12;
    public final int screenHeight = tileSize * 22;

    private long startTime;
    private double currentTime = 0;

    // Game state variable to track if you're in the menu or in-game
    String gameState = "menu";

    // The Network class has to be an object or else function calls would be static, which won't work for object-oriented
    public Network network = new Network(this);

    // Sound objects to play sfx and music
    Sound se = new Sound();
    Sound music = new Sound();

    // Create the tile manager
    TileManager tileM = new TileManager(this);

    // Create the collisionHandler with the tile manager
    CollisionHandler collisionHandler = new CollisionHandler(this, tileM);

    // Create the player object, which doesn't update until player.initialize is called
    Player player = new Player(this, collisionHandler, network);

    // Create the menu instance
    public Menu menu = new Menu(this, network);

    // Create the custom key handler
    public KeyHandler keyH = new KeyHandler(this, player, menu);

    // Create the array that the squares for the transition animation will be stored in
    public List<Point> grayBlocksAnim = new ArrayList<>();


    //constructor
    public GamePanel() throws IOException {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.LIGHT_GRAY);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        startup();
    }

    private void startup() {
        // Make the gamepanel run on its own thread
        thread = new Thread(this);
        thread.start();
    }


    @Override
    public void run() {
        // Continuously run the draw and update functions
        double drawInterval = 1000000000 / FPS;

        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (thread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }

    }

    private void update() {
        // All the update functions ended up being unused
        switch (gameState) {
            case "menu":
                menu.update();
                break;
            case "game":
                player.update();
                break;
        }
    }

    public void playSE(int index) {
        // Play a sound file once
        se.setFile(index);
        se.play();
    }

    public void playMusic(int index) {
        // Play a sound file and loop it
        music.setFile(index);
        music.play();
        music.loop();
    }

    public void stopMusic() {
        // Stop the currently playing music
        music.stop();
    }


    public void paintComponent(Graphics g) {
        // Draw everything from the draw function to the screen
        Graphics2D g2 = (Graphics2D) g;
        super.paintComponent(g2);
        draw(g2);

    }

    private void draw(Graphics2D g2) {
        // Draw the menu or game based on the game state
        switch (gameState) {
            case "menu":
                menu.draw(g2);
                break;
            case "game":
                try {
                    tileM.draw(g2);
                    player.draw(g2);
                } catch (Exception e) {
//                throw new RuntimeException(e);
                }
                break;
        }

        // Draw the gray blocks animation
        try {
            synchronized (grayBlocksAnim) {
                for (Point square : grayBlocksAnim) {
                    g2.drawImage(player.gray, square.x, square.y, tileSize, tileSize, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
