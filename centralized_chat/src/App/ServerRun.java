package App;

import Records.NodeInfo;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import PropertyHandler.PropertyHandler;
import java.util.Properties;

public class ServerRun {
    private static ServerSocket serverSocket;
    private static int port;
    public static List<NodeInfo> nodeList = new ArrayList<NodeInfo>(); // Used only in Server.

    // Constructor for ServerRuns, sets up socket and port.
    public ServerRun(String propertiesFile) {
        Properties properties = null;
        int port = -1;

        // Open properties.
        try {
            properties = new PropertyHandler(propertiesFile);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "Cannot open properties file", ex);
            System.exit(1);
        } 

        // Get server port number.
        try {
            port = Integer.parseInt(properties.getProperty("SERVER_PORT"));
        } catch (NumberFormatException ex){
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "Cannot read server port", ex);
            System.exit(1);
        }

        // Try initializing server.
        try {
            ServerRun.port = port;
            ServerRun.serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(ServerRun.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Error starting server on port " + port);
            System.exit(1);
        }
    }

    // Main server loop. Accepts a client socket and starts a Server thread for it.
    public void runServerLoop() throws IOException {
        new Thread(new Server(serverSocket)).start();
    }

    // Starts the server and runs the server loop.
    public static void main(String args[]) throws Exception {
        ServerRun serverThread = new ServerRun("server.properties");
        serverThread.runServerLoop();
    }
}

