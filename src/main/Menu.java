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
        menuOptions = Arrays.asList("Single Player", "Multiplayer");
        selectedOption = 0;

        status = "";

        try {
            logo = ImageIO.read(getClass().getResourceAsStream("/sprites/tetris-logo.png"));
            bg = ImageIO.read(getClass().getResourceAsStream("/sprites/menuBG0.jpg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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

    public void selectOption() {
        String port;

        switch (menuOptions.get(selectedOption)) {
            case "Single Player":
                gp.gameState = "game";
                gp.player.grayBlocksAnimation(gp.grayBlocksAnim, false);
                gp.player.initialize();
                break;
            case "Multiplayer":
                selectedOption = 0;
                menuOptions = Arrays.asList("Host", "Join", "Back");
                break;
            case "Host":
                port = JOptionPane.showInputDialog(null, "Enter the port to host on\n\nCommon hosting ports:\n12345\n27015\n27016\n7777");
                if (port == null || port.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Error: No port entered", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    network.port = Integer.parseInt(port);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error: Invalid port entered", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                status = "hosting";
                menuOptions = Arrays.asList(" Back ");
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
                yourIP = "";
                try {
                yourIP = InetAddress.getLocalHost().getHostAddress();
                System.out.println(InetAddress.getLocalHost().getHostAddress()); } catch (UnknownHostException e) {
//                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error: Couldn't show your IP\nUse the terminal to find it", "Error", JOptionPane.ERROR_MESSAGE);
                }
                new Thread(() -> {
                    while (true) {
//                        System.out.println("connected = " + network.connected);
                        if (status.equals("hosting") && network.connected) {
                            menuOptions = Arrays.asList("Start", " Back ");
                            status = "host player";
//                            System.out.println("connected = " + network.connected);
                            return;
                        }
                    }
                }).start();
                break;
            case "Join":
                selectedOption = 0;
                menuOptions = Arrays.asList("Same PC", "LAN", " Back ");
                break;
            case "Same PC":
                status = "joining";
                menuOptions = Arrays.asList(" Back ");
                network.ip = "localhost";

                port = JOptionPane.showInputDialog(null, "Enter the Host's port");
                if (port == null || port.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Error: No port entered", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    network.port = Integer.parseInt(port);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error: Invalid port entered", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

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
                String ip = JOptionPane.showInputDialog(null, "Enter the Host's IP Address");
                if (ip == null || ip.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Error: No IP address entered", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                network.ip = ip;

                port = JOptionPane.showInputDialog(null, "Enter the Host's port");
                if (port == null || port.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Error: No port entered", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    network.port = Integer.parseInt(port);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error: Invalid port entered", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

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
                selectedOption = 0;
                menuOptions = Arrays.asList("Single Player", "Multiplayer");
                break;
            case " Back ":
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

                status = "";
                selectedOption = 0;
                menuOptions = Arrays.asList("Host", "Join", "Back");
                break;

            case "Start":
                network.sendMessage(network.out, "start");
                gp.gameState = "game";
                gp.player.grayBlocksAnimation(gp.grayBlocksAnim, false);
                gp.player.initialize();
        }
    }

    public void gameOver(boolean youLost) {
        gp.player.grayBlocksAnimation(gp.grayBlocksAnim, false);

        gp.gameState = "menu";
        winner = youLost ? "p2" : "p1"; // inline if statement

        if (status.equals("hosting") || status.equals("host player")) {
            menuOptions = Arrays.asList("Start", " Back ");
        }
        if (status.equals("joined")) {
            menuOptions = Arrays.asList(" Back ");
        }
    }

    public void disconnected() {
        try {
            gp.player.remove();
        } catch (NullPointerException e) {

        }
        gp.player.grayBlocksAnimation(gp.grayBlocksAnim, false);
        gp.gameState = "menu";
        menuOptions = Arrays.asList("Single Player", "Multiplayer");
        status = "disconnected";
        winner = "";
    }

    public void update() {

    }

    public void draw(Graphics2D g2) {
        g2.drawImage(bg, 0, 0, gp.screenWidth, gp.screenHeight, null);
        g2.drawImage(logo, (gp.screenWidth - (1000 / (gp.tileSize / 5))) / 2, gp.tileSize / 2, 1000 / (gp.tileSize / 5), 694 / (gp.tileSize / 5), null);

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
//        try {
//            for (Point square : gp.grayBlocksAnim) {
//                g2.drawImage(gp.player.gray, square.x, square.y, gp.tileSize, gp.tileSize, null);
//            }
//        } catch (Exception e) {
//
//        }
    }
}
