package entity;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PlacedBlock extends Entity {
    public PlacedBlock(int posX, int posY, List<Point> squares, String blockType) {
        this.posX = posX;
        this.posY = posY;
        this.squares = new ArrayList<>(squares);
        this.currentBlock = blockType;
        for (Point square : squares) {
            this.squares.add(new Point(square.x, square.y));
        }
    }

    public List<Point> getAbsoluteSquares() {
        List<Point> absoluteSquares = new ArrayList<>();
        for (Point square : squares) {
            absoluteSquares.add(new Point(posX + square.x, posY + square.y));
        }
        return absoluteSquares;
    }
}
