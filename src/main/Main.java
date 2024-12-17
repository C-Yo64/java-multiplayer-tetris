package main;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        //Create a frame
        JFrame window = new JFrame("Tetris");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        //add our gamepanel to our frame
        GamePanel gp = new GamePanel();
        window.add(gp);
        window.pack();

        //make the frame visible
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        ImageIcon img = new ImageIcon(Main.class.getResource("/sprites/tetris-logo.png"));
        window.setIconImage(img.getImage());
    }
}