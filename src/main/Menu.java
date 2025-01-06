package main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.net.InetAddress;

public class Menu {

    GamePanel gp;
    Network network;

    public BufferedImage logo;
    public BufferedImage bg;

    List<String> menuOptions;
    int selectedOption;

    String status;
    public String winner = "";

    Thread networkThread;

    public String yourIP;

//    List<Point> grayBlocksAnim = new ArrayList<>();

    public Menu(GamePanel gp, Network network) {
        this.gp = gp;
        this.network = network;

        initialize();
    }

    private void initialize() {
        // The menuOptions stores an array of all the current buttons on the menu
        // The selectedOption stores the one that's currently being highlighted
        menuOptions = Arrays.asList("Single Player", "Multiplayer");
        selectedOption = 0;

        // Status determines the text displayed at the bottom of the screen
        // For example, when connecting to a host, it will display "Connecting to [host IP]"
        status = "";

        // Set up images
        try {
            logo = ImageIO.read(getClass().getResourceAsStream("/sprites/tetris-logo.png"));
            bg = ImageIO.read(getClass().getResourceAsStream("/sprites/menuBG0.jpg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // This function is called on key presses
    // It just increases/decreases the selected option, which scrolls through the menu
    public void changeOption(String direction) {
        switch (direction) {
            case "up":
                selectedOption = (selectedOption - 1 + menuOptions.size()) % menuOptions.size();
                break;
            case "down":
                selectedOption = (selectedOption + 1) % menuOptions.size();
                break;
        }
    }

    // This is called when the user presses enter
    // It gets the currently selected menu option, and runs the code corresponding to it
    public void selectOption() {
        String port;

        switch (menuOptions.get(selectedOption)) {
            case "Single Player":
                // Start the game in single-player mode, and plays the transition animation
                gp.gameState = "game";
                gp.player.grayBlocksAnimation(gp.grayBlocksAnim, false);
                gp.player.initialize();
                break;
            case "Multiplayer":
                // Goes to the multiplayer selection screen
                selectedOption = 0;
                menuOptions = Arrays.asList("Host", "Join", "Back");
                break;
            case "Host":
                // Begins hosting a multiplayer game on the specified port number

                // Show a JFrame text field where the user can enter the port number to host on
                port = JOptionPane.showInputDialog(null, "Enter the port to host on\n\nCommon hosting ports:\n12345\n27015\n27016\n7777");
                if (port == null || port.trim().isEmpty()) {
                    // Show an error if no port number gets entered, and stop hosting
                    JOptionPane.showMessageDialog(null, "Error: No port entered", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    // Get the number entered in the text field and assign it to the network port
                    network.port = Integer.parseInt(port);
                } catch (NumberFormatException e) {
                    // Show an error if an integer can't be extracted from the text entered
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error: Invalid port entered", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Set the menu to indicate that you're hosting a game
                status = "hosting";
                menuOptions = Arrays.asList(" Back ");

                // Start hosting a game on the specified port number
                networkThread = new Thread(() -> {
                    try {
                        network.Host();
                    } catch (IOException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error: Couldn't start hosting", "Error", JOptionPane.ERROR_MESSAGE);
                        status = "";
                    }
                });
                networkThread.start();

                // Get the computer's IP and display it
                yourIP = "";
                try {
                yourIP = InetAddress.getLocalHost().getHostAddress();
                System.out.println(InetAddress.getLocalHost().getHostAddress()); } catch (UnknownHostException e) {
//                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error: Couldn't show your IP\nUse the terminal to find it", "Error", JOptionPane.ERROR_MESSAGE);
                }

                // Repeatedly check if the other player has connected, and update the menu when connected
                // Run this on another thread so the menu can still run
                new Thread(() -> {
                    while (true) {
                        if (status.equals("hosting") && network.connected) {
                            menuOptions = Arrays.asList("Start", " Back ");
                            status = "host player";
                            return;
                        }
                    }
                }).start();
                break;
            case "Join":
                // Change the menu to show options for joining another player
                selectedOption = 0;
                menuOptions = Arrays.asList("Same PC", "LAN", " Back ");
                break;
            case "Same PC":
                // Automatically sets the IP to connect to the same computer, then joins a game hosted there

                // Set the menu to indicate joining
                status = "joining";
                menuOptions = Arrays.asList(" Back ");

                // Set the IP to localhost (the same as 127.0.0.1) which sets it to look for other programs on the same computer
                network.ip = "localhost";

                // Show a JFrame text field where the user can enter the port number to join
                port = JOptionPane.showInputDialog(null, "Enter the Host's port");
                if (port == null || port.trim().isEmpty()) {
                    // Show an error if no port number gets entered, and stop joining
                    JOptionPane.showMessageDialog(null, "Error: No port entered", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    // Get the number entered in the text field and assign it to the network port
                    network.port = Integer.parseInt(port);
                } catch (NumberFormatException e) {
                    // Show an error if an integer can't be extracted from the text entered
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error: Invalid port entered", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Try to join the host game on the port entered, and show an error if it doesn't work
                networkThread = new Thread(() -> {
                    try {
                        network.Client();
                    } catch (IOException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error: Couldn't connect to the host", "Error", JOptionPane.ERROR_MESSAGE);
                        status = "";
                    }
                });
                networkThread.start();

                // Continuously check if it's connected, and update the menu once it is
                new Thread(() -> {
                    while (true) {
                        if (network.connected) {
                            status = "joined player";
                            return;
                        }
                    }
                }).start();
                break;
            case "LAN":
                // Prompts the user to enter the local IP and port of the host, then joins the game hosted there

                // Show a JFrame text field where the user can enter the IP address to join
                String ip = JOptionPane.showInputDialog(null, "Enter the Host's IP Address");
                if (ip == null || ip.trim().isEmpty()) {
                    // Show an error if no IP gets entered, and stop joining
                    JOptionPane.showMessageDialog(null, "Error: No IP address entered", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Set the IP to join to the one that was entered
                network.ip = ip;

                // Show a JFrame text field where the user can enter the port number to join
                port = JOptionPane.showInputDialog(null, "Enter the Host's port");
                if (port == null || port.trim().isEmpty()) {
                    // Show an error if no port number gets entered, and stop joining
                    JOptionPane.showMessageDialog(null, "Error: No port entered", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    // Get the number entered in the text field and assign it to the network port
                    network.port = Integer.parseInt(port);
                } catch (NumberFormatException e) {
                    // Show an error if an integer can't be extracted from the text entered
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error: Invalid port entered", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Try to join the host game on the port entered, and show an error if it doesn't work
                status = "joining";
                menuOptions = Arrays.asList(" Back ");
                networkThread = new Thread(() -> {
                    try {
                        network.Client();
                    } catch (IOException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error: Couldn't connect to the host", "Error", JOptionPane.ERROR_MESSAGE);
                        status = "";
                    }
                });

                // Continuously check if it's connected, and update the menu once it is
                networkThread.start();
                new Thread(() -> {
                    while (true) {
                        if (network.connected) {
                            status = "joined player";
                            return;
                        }
                    }
                }).start();
                break;

            case "Back":
                // Return to the first menu
                selectedOption = 0;
                menuOptions = Arrays.asList("Single Player", "Multiplayer");
                break;
            case " Back ":
                // Stop all network functions and return to the multiplayer menu
                // The string " Back " looks the same as "Back" when displayed to the center of the screen, but since it's a different string, I can assign different functions to when it's selected

                // Check if any network functions are still running and if any input/output streams are still open, and if so, stop them
                try {
                    if (networkThread != null && networkThread.isAlive()) {
                        if (network.serverSocket != null && !network.serverSocket.isClosed()) {
                            network.serverSocket.close();
                        }
                        if (network.clientSocket != null && !network.clientSocket.isClosed()) {
                            network.clientSocket.close();
                        }
                        networkThread.interrupt();
                    }
                    if (network.out != null) {
                        network.in.close();
                        network.in = null;
                        network.out.close();
                        network.out = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Go back to the multiplayer menu
                status = "";
                selectedOption = 0;
                menuOptions = Arrays.asList("Host", "Join", "Back");
                break;

            case "Start":
                // Send a signal to the other player to start then game, and then start it with the transition animation
                network.sendMessage(network.out, "start");
                gp.gameState = "game";
                gp.player.grayBlocksAnimation(gp.grayBlocksAnim, false);
                gp.player.initialize();
        }
    }

    public void gameOver(boolean youLost) {
        // Goes back to the main menu and displays who won

        // Play the transition animation
        gp.player.grayBlocksAnimation(gp.grayBlocksAnim, false);

        // Load the menu with the winner updating depending on the youLost parameter
        // In multiplayer, the person who lost runs the function with youLost as true, and sends a message to the other player to run the function with youLost as false
        gp.gameState = "menu";
        winner = youLost ? "p2" : "p1"; // inline if statement, p1 is always the current player and p2 is the other player

        // If you're the host, show the start button, otherwise just show the back button
        if (status.equals("hosting") || status.equals("host player")) {
            menuOptions = Arrays.asList("Start", " Back ");
        }
        if (status.equals("joined")) {
            menuOptions = Arrays.asList(" Back ");
        }
    }

    public void disconnected() {
        // Stop running the game and go back to the main menu
        // This function runs if at any point during multiplayer you are disconnected from the other player, or if you exit while in single player

        // Stop the game from running (in a try catch because you could be in the menu when this runs, so the player object wouldn't be initialized)
        try {
            gp.player.remove();
        } catch (NullPointerException e) {

        }

        // Play the transition animation and go to the main menu
        gp.player.grayBlocksAnimation(gp.grayBlocksAnim, false);
        gp.gameState = "menu";
        menuOptions = Arrays.asList("Single Player", "Multiplayer");
        status = "disconnected";
        winner = "";
    }

    public void update() {

    }

    public void draw(Graphics2D g2) {
        // Draw the background and logo first
        g2.drawImage(bg, 0, 0, gp.screenWidth, gp.screenHeight, null);
        g2.drawImage(logo, (gp.screenWidth - (1000 / (gp.tileSize / 5))) / 2, gp.tileSize / 2, 1000 / (gp.tileSize / 5), 694 / (gp.tileSize / 5), null);

        // Show all the current menu options in the middle of the screen, with the selected one being bolded
        for (int i = 0; i < menuOptions.size(); i++) {
            if (i == selectedOption) {
                g2.setFont(new Font("Arial", Font.BOLD, 20));
            } else {
                g2.setFont(new Font("Arial", Font.PLAIN, 20));
            }
            g2.setColor(Color.lightGray);
            FontMetrics fontMetrics = g2.getFontMetrics();
            g2.drawString(menuOptions.get(i), (gp.screenWidth - fontMetrics.stringWidth(menuOptions.get(i))) / 2, ((gp.screenHeight / 2) + (i * 50)) - menuOptions.size() * 20);
        }

        // Show the status text at the bottom of the screen
        // The status text shows what the game is doing. For example, when joining a player, the status text will show "Joining" and update once you've joined
        String string = "";
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fontMetrics = g2.getFontMetrics();
        g2.setColor(Color.black);
        switch (status) {
            case "hosting":
                string = "Your IP: " + yourIP + " on port " + network.port;
                g2.drawString(string, (gp.screenWidth - fontMetrics.stringWidth(string)) / 2, gp.screenHeight - (int) (fontMetrics.getHeight() * 2.5));
                string = "Waiting for a player to join";
                break;
            case "joining":
                string = "Joining " + network.ip + ":" + network.port;
                break;
            case "host player":
                string = "Connected, press Start";
                break;
            case "joined player":
                string = "Waiting for host to start";
                break;
            case "disconnected":
                string = "Disconnected";
                break;
        }
        g2.drawString(string, (gp.screenWidth - fontMetrics.stringWidth(string)) / 2, gp.screenHeight - (int) (fontMetrics.getHeight() * 1.5));

        // Show who won the last game just above the status text
        string = "";
        switch (winner) {
            case "p1":
                string = "You Won!";
                break;
            case "p2":
                string = "You Lost";
                break;
        }
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        fontMetrics = g2.getFontMetrics();
        g2.drawString(string, (gp.screenWidth - fontMetrics.stringWidth(string)) / 2, gp.screenHeight - (int) (fontMetrics.getHeight() * 2.5));
    }
}
