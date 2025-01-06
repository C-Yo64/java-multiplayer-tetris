package main;

import entity.Entity;
import entity.Player;
import main.Network;

import java.awt.event.*;

public class KeyHandler implements KeyListener {

    GamePanel gp;
    Player player;
    Network network;
    Menu menu;

    public KeyHandler(GamePanel gp, Player player, Menu menu) {
        this.gp = gp;
        this.player = player;
        this.network = gp.network;
        this.menu = menu;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

        // Get the key that was pressed
        int code = e.getKeyCode();

        // The actions that are available change based on if the player is in game or in the menu
        switch (gp.gameState) {
            case "menu":
                switch (code) {
                    // Navigate through the menu
                    case KeyEvent.VK_W, KeyEvent.VK_UP:
                        menu.changeOption("up");
                        break;
                    case KeyEvent.VK_S, KeyEvent.VK_DOWN:
                        menu.changeOption("down");
                        break;

                    case KeyEvent.VK_ENTER:
                        menu.selectOption();
                }
                break;
            case "game":
                switch (code) {
                    // Move the block
                    case KeyEvent.VK_A:
                        player.moveBlock("left");
                        break;
                    case KeyEvent.VK_D:
                        player.moveBlock("right");
                        break;
                    case KeyEvent.VK_S:
                        player.moveBlock("down");
                        break;

                    // Rotate the block
                    case KeyEvent.VK_R:
                        player.rotateBlock();
                        break;

                    // If in single player, exit to the main menu
                    case KeyEvent.VK_ESCAPE:
                        if (!network.connected) {
                            menu.disconnected();
                        }
                        break;

                    // Drop the current block
                    case KeyEvent.VK_SPACE:
                        player.dropBlock();
                        break;


                    // If playing multiplayer on the same computer and you are the host (when hosting the localhost IP is always 127.0.0.1), send inputs to the other player
                    case KeyEvent.VK_LEFT:
                        if (network.connected && network.serverSocket != null && network.clientSocket.getInetAddress().toString().equals("/127.0.0.1")) {
                            network.sendMessage(network.out, "left");
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (network.connected && network.serverSocket != null && network.clientSocket.getInetAddress().toString().equals("/127.0.0.1")) {
                            network.sendMessage(network.out, "right");
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (network.connected && network.serverSocket != null && network.clientSocket.getInetAddress().toString().equals("/127.0.0.1")) {
                            network.sendMessage(network.out, "down");
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if (network.connected && network.serverSocket != null && network.clientSocket.getInetAddress().toString().equals("/127.0.0.1")) {
                            network.sendMessage(network.out, "rotate");
                        }
                        break;
                    case KeyEvent.VK_CONTROL:
                        if (network.connected && network.serverSocket != null && network.clientSocket.getInetAddress().toString().equals("/127.0.0.1")) {
                            network.sendMessage(network.out, "drop");
                        }
                        break;


                    // testing code
                    case KeyEvent.VK_1:
//                        player.grayLines(1);
                        break;
                    case KeyEvent.VK_2:
//                        player.grayLines(2);
                        break;
                    case KeyEvent.VK_3:
//                        player.grayLines(3);
                        break;
                    case KeyEvent.VK_4:
//                        player.grayLines(5);
//                        network.sendMessage(network.out, "4");
                        break;

                    case KeyEvent.VK_H:
//                        player.gameOver();
                        break;
//                    case KeyEvent.VK_C:
//                        network.Client();
//                        break;
                }
                break;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

}
