package main;

import entity.PlacedBlock;
import tile.TileManager;

import java.awt.*;
import java.util.List;

public class CollisionHandler {

    GamePanel gp;
    TileManager tileManager;


    public CollisionHandler(GamePanel gp, TileManager tileManager) {
        this.gp = gp;
        this.tileManager = tileManager;
    }

    public boolean isColliding(int posX, int posY, int size, List<Point> squares, List<PlacedBlock> placedBlocks) {
        int tileSize = gp.tileSize;

        for (Point square : squares) {

            // Calculate the absolute position of the square
            int squareX = posX + square.x;
            int squareY = posY + square.y;

            // Calculate the tile positions for each corner of the square
            int leftTile = squareX / tileSize;
            int rightTile = (squareX + size - 1) / tileSize;
            int topTile = squareY / tileSize;
            int bottomTile = (squareY + size - 1) / tileSize;


            // Check each corner to see if it's in a collidable tile
            if (isTileCollidable(topTile, leftTile) ||
                    isTileCollidable(topTile, rightTile) ||
                    isTileCollidable(bottomTile, leftTile) ||
                    isTileCollidable(bottomTile, rightTile)) {
                return true;
            }

            // Return true if the position of the square is the same as the position of one of the placed squares
            for (PlacedBlock block : placedBlocks) {
                for (Point placedSquare : block.getAbsoluteSquares()) {
                    if (squareX == placedSquare.x && squareY == placedSquare.y) {
                        return true;
                    }
                }
            }
        }

        // If nothing returns true, return false
        return false;
    }

    // Helper method to check if a specific tile position is collidable
    private boolean isTileCollidable(int row, int col) {
        // Boundary check
        if (row >= 0 && row < tileManager.mapTileNum.length && col >= 0 && col < tileManager.mapTileNum[0].length) {
            int tileNum = tileManager.mapTileNum[row][col];
            return tileManager.tiles[tileNum].collision;
        }
        return false; // Out of bounds (treat as non-collidable)
    }
}
