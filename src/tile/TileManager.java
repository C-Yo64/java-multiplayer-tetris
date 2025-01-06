package tile;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import main.GamePanel;

public class TileManager {

    GamePanel gp;
    public Tile[] tiles;
    public int[][] mapTileNum;

    public TileManager(GamePanel gp) {
        // Load the tiles from the map
        this.gp = gp;
        tiles = new Tile[2];
        mapTileNum = new int[gp.screenHeight / gp.tileSize][gp.screenWidth / gp.tileSize];
        loadTiles();
        loadMap("/maps/map_1.txt");
    }

    private void loadTiles() {
        // Set the color and collision settings for the different tile types
        tiles[0] = new Tile();
        tiles[0].collision = false;
        tiles[0].color = Color.lightGray;

        tiles[1] = new Tile();
        tiles[1].collision = true;
        tiles[1].color = Color.darkGray;
    }

    private void loadMap(String filePath) {
        // Load the tiles and their types from a text file

        System.out.println("loading map from " + filePath);
        try {
            // Read the file
            InputStream is = getClass().getResourceAsStream(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            // Iterate through each line of text and add tiles in that line for each number
            int row = 0;
            String line;
            while (row < mapTileNum.length && (line = br.readLine()) != null) {
                String[] numbers = line.split(" ");

                for (int col = 0; col < numbers.length && col < mapTileNum[row].length; col++) {
                    int num = Integer.parseInt(numbers[col]);
                    mapTileNum[row][col] = num;
                }
                row++;
            }
            br.close();
        } catch (Exception e) {
            System.err.println("Error loading map: " + e.getMessage());
        }
    }


    public void draw(Graphics2D g2) {
        // Draw the tiles to the screen
        for (int row = 0; row < mapTileNum.length; row++) {
            for (int col = 0; col < mapTileNum[row].length; col++) {
                int tileNum = mapTileNum[row][col];

                // Calculate the position of the tile on screen
                int x = col * gp.tileSize;
                int y = row * gp.tileSize;

                // Set the color based on the tile type and draw the tile
                g2.setColor(tiles[tileNum].color);
                g2.fillRect(x, y, gp.tileSize, gp.tileSize);
            }
        }
    }


}
