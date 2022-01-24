import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.Map;
import java.util.HashMap;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerThread{

    private static ServerSocket serverSocket;
    private static int port;
    public static HashMap<NodeInfo, Socket> nodeList = new HashMap<NodeInfo, Socket>();
    Socket clientSocket = null;

    public ServerThread(int port) {
        try {
            ServerThread.port = port;
            ServerThread.serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Error starting server on port " + port);
            System.exit(1);
        }
        ServerThread.port = port;
    }

    public void runServerLoop() throws IOException {
        while (true) {
            // System.out.println("Waiting for connections on port #" + port);

            clientSocket = serverSocket.accept();
            new Thread(new Server(clientSocket)).start();
        }
    }

    public static void main(String args[]) throws Exception {
        ServerThread ServerThread = new ServerThread(23657);
        ServerThread.runServerLoop();
    }
}
