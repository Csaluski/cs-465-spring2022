package App;

import Records.NodeInfo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.HashMap;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerRun {

    private static ServerSocket serverSocket;
    private static int port;
    public static HashMap<NodeInfo, Socket> nodeList = new HashMap<NodeInfo, Socket>(); //used only in Server
    Socket listenSocket = null;

    // Constructor for ServerRuns, sets up socket and port.
    public ServerRun(int port) {
        try {
            ServerRun.port = port;
            ServerRun.serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(ServerRun.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Error starting server on port " + port);
            System.exit(1);
        }
        ServerRun.port = port;
    }

    // Main server loop. Accepts a client socket and starts a Server thread for it.
    public void runServerLoop() throws IOException {
        while (true) {
            // System.out.println("Waiting for connections on port #" + port);
            new Thread(new Server(serverSocket.accept())).start();
        }
    }

    // Starts the server and runs the server loop.
    public static void main(String args[]) throws Exception {
        ServerRun serverThread = new ServerRun(23657);
        serverThread.runServerLoop();
    }
}

//TODO ROSZE double check everything is commented properly

