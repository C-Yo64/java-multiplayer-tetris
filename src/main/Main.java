package main;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        //Create a JFrame window
        JFrame window = new JFrame("Tetris");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        // Create a gamepanel object and add it to the frame
        GamePanel gp = new GamePanel();
        window.add(gp);
        window.pack();

        // Make the frame visible and centered
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        // Add the window/taskbar icon
        ImageIcon img = new ImageIcon(Main.class.getResource("/sprites/tetris-logo.png"));
        window.setIconImage(img.getImage());
    }
}