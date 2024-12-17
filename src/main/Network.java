package main;

import entity.Player;

import java.io.*;
import java.net.*;

public class Network {
    Player player;
    GamePanel gp;

    int port;
    String ip;

    public ServerSocket serverSocket;
    public Socket clientSocket;

    public DataInputStream in;
    public DataOutputStream out;

    public volatile boolean connected;

    public Network(GamePanel gp) {
//        this.player = player;
        this.gp = gp;

//        port = 12345;
    }

    public void Host() throws IOException {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                serverSocket = null;
            }
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
                clientSocket = null;
            }

            if (out != null) {
                in.close();
                in = null;
                out.close();
                out = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
//            serverSocket = new ServerSocket(port);
            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(port));

            System.out.println("Host started. Waiting for a client to connect...");

            clientSocket = serverSocket.accept();
            System.out.println("Client connected from " + clientSocket.getInetAddress());
            connected = true;

            // Set up input and output streams
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());

            // Start a thread to listen for incoming messages
            new Thread(() -> listenForMessages(in)).start();

//            sendMessages(out);

        } catch (IOException e) {
            connected = false;
            if (Thread.currentThread().isInterrupted()) {
                System.out.println("Host interrupted");
            } else {
                e.printStackTrace();
                throw new IOException();
            }
        }
    }

    public void Client() throws IOException {
        try {
            clientSocket = new Socket(ip, port);
            clientSocket.setReuseAddress(true);

            System.out.println("Connected to the host.");
            connected = true;

            // Set up input and output streams
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());

            // Start a thread to listen for incoming messages
            new Thread(() -> listenForMessages(in)).start();

            // Main thread handles sending messages
//        sendMessages(out);

        } catch (IOException e) {
            connected = false;
            if (Thread.currentThread().isInterrupted()) {
                System.out.println("Client interrupted");
            } else {
                e.printStackTrace();
                throw new IOException();
            }
        }
    }

    public void listenForMessages(DataInputStream in) {
        try {
            while (true) {
                String message = in.readUTF();
                System.out.println("Peer: " + message);
                switch (message) {
                    case "start":
                        gp.gameState = "game";
                        gp.player.grayBlocksAnimation(gp.grayBlocksAnim, false);
                        gp.player.initialize();
                        break;
                    case "game over":
                        gp.player.remove();
                        gp.menu.gameOver(false);
                        break;

                    case "2":
                        gp.player.grayLines(1);
                        break;
                    case "3":
                        gp.player.grayLines(2);
                        break;
                    case "4":
                        gp.player.grayLines(3);
                        break;

                    case "left":
                        gp.player.moveBlock("left");
                        break;
                    case "right":
                        gp.player.moveBlock("right");
                        break;
                    case "rotate":
                        gp.player.rotateBlock();
                        break;
                    case "drop":
                        gp.player.dropBlock();
                }
            }
        } catch (IOException e) {
            System.out.println("Peer disconnected.");
            connected = false;
            gp.menu.disconnected();
        }
    }

    public void sendMessage(DataOutputStream out, String message) {
        try {
            out.writeUTF(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        try (BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {
//            String input;
//            while ((input = userInput.readLine()) != null) {
//                out.writeUTF(input);
//                out.flush();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
