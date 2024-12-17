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

    String gameState = "menu";

    public Network network = new Network(this);

    Sound se = new Sound();
    Sound music = new Sound();

    //This is our tile Manager object being created
    TileManager tileM = new TileManager(this);

    //Our collisionHandler will be constructed with the Tile Manager
    CollisionHandler collisionHandler = new CollisionHandler(this, tileM);

    //the player will now receive the collision handler.
    Player player = new Player(this, collisionHandler, network);

    public Menu menu = new Menu(this, network);

    public KeyHandler keyH = new KeyHandler(this, player, menu);

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

        thread = new Thread(this);
        thread.start();
    }


    @Override
    public void run() {

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
        se.setFile(index);
        se.play();
    }

    public void playMusic(int index) {
        music.setFile(index);
        music.play();
        music.loop();
    }

    public void stopMusic() {
        music.stop();
    }


    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        super.paintComponent(g2);
        draw(g2);

    }

    private void draw(Graphics2D g2) {
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
