import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.Map;
import java.util.HashMap;

import java.util.logging.Level;
import java.util.logging.Logger;

import utils.PropertyHandler;
import java.util.Properties;

public class Server{

    private static ServerSocket serverSocket;
    private static int port = 0;
    public static HashMap<NodeInfo, Socket> nodeList = new HashMap<NodeInfo, Socket>();
    Socket clientSocket = null;

    /**
     * Constructor
     * 
     * @param propertiesFile String of a file on relative path containing properties
     */
    public Server(String propertiesFile) {
        Properties properties = null;
        
        // open properties
        try {
            properties = new PropertyHandler(propertiesFile);
        }
        catch (IOException ex)
        {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "Cannot open properties file", ex);
            System.exit(1);
        } 

        // get server port number
        try {
            port = Integer.parseInt(properties.getProperty("SERVER_PORT"));
        }
        catch (NumberFormatException ex)
        {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "Cannot read server port", ex);
            System.exit(1);
        }

        // create server socket
        try {
            Server.serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Error starting server on port " + port);
            System.exit(1);
        }
        Server.port = port;
    }

    public void runServerLoop() throws IOException {
        while (true) {
            // System.out.println("Waiting for connections on port #" + port);

            clientSocket = serverSocket.accept();
            new Thread(new ServerThread(clientSocket)).start();
        }
    }

    public static void main(String args[]) throws Exception {
        Server Server = new Server("src/config/Server.properties");
        Server.runServerLoop();
    }
}
