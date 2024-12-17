package main;

import entity.Entity;
import entity.Player;
import main.Network;

import java.awt.event.*;

//we want to implement the interface called KeyListener
//which is a class that listens for interactions with
//your keyboard. In order to implement this interface
//we need to override several methods seen below
public class KeyHandler implements KeyListener {

    //we are going to give access to the variables from
    //our gamepanel, just like we did for the player
    GamePanel gp;
    Player player;
    Network network;
    Menu menu;


    //lets make our constructor and require the GamePanel
    //as a parameter when creating our KeyHandler object
    public KeyHandler(GamePanel gp, Player player, Menu menu) {
        this.gp = gp;
        this.player = player;
        this.network = gp.network;
        this.menu = menu;
    }


    //This one deals with typing, which we are going
    //to leave blank for this project
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

        int code = e.getKeyCode();

        switch (gp.gameState) {
            case "menu":
                switch (code) {
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
                    case KeyEvent.VK_A:
                        player.moveBlock("left");
                        break;
                    case KeyEvent.VK_D:
                        player.moveBlock("right");
                        break;
                    case KeyEvent.VK_S:
                        player.moveBlock("down");
                        break;

                    case KeyEvent.VK_W:
//                lastPressedKey = "up";
//                        player.moveBlock("down");
//                        break;
                    case KeyEvent.VK_R:
                        player.rotateBlock();
                        break;

                    case KeyEvent.VK_ESCAPE:
                        if (!network.connected) {
                            menu.disconnected();
                        }
                        break;

                    case KeyEvent.VK_SPACE:
//                        player.randomizeBlock();
//                        player.ghostBlock();
                        player.dropBlock();
                        break;


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

        //we copy the above contents and change the booleans to
        //false when the key gets released.
        int code = e.getKeyCode();

//        if (code == KeyEvent.VK_A) {
//            leftP = false;
//            lastPressedKey = null;
//        }
//        if (code == KeyEvent.VK_D) {
//            rightP = false;
//            lastPressedKey = null;
//        }
//        if (code == KeyEvent.VK_S) {
//            downP = false;
//            lastPressedKey = null;
//        }
//        if (code == KeyEvent.VK_W) {
//            upP = false;
//            lastPressedKey = null;
//        }
    }

}
