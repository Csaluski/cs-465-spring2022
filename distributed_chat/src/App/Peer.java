package App;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Inet4Address;
import java.util.List;
import java.util.ArrayList;
import Records.NodeInfo;

// Object to make a chat user.
public class Peer {
    public static List<NodeInfo> nodeList = new ArrayList<NodeInfo>(); // Used only in Server.
    private final ServerSocket listenSocket;
    private final NodeInfo nodeInfo;

    // Controller for specific instance of chat client.
    private Peer(String name) throws IOException {
        // Connection stuff.
        ServerSocket listenSocketTemp;
        Inet4Address address = (Inet4Address) Inet4Address.getByName(IPUtils.getMyIP());
        int listenPort = -1;
        listenSocketTemp = null;
        // Opens a listening port on an unused port, then takes the port number to use later in the socket loop.
        try (ServerSocket socket = new ServerSocket(0)) {
            listenPort = socket.getLocalPort();
            socket.close();
            listenSocketTemp = new ServerSocket(listenPort);
        } catch (IOException e) {
            System.out.println(e);
        }
        listenSocket = listenSocketTemp;
        this.nodeInfo = new NodeInfo(address, listenPort, name);

        // PropertyHandler propReader = new PropertyHandler("config/Server.properties");
        // Inet4Address serverAddr = (Inet4Address) Inet4Address.getByName(propReader.getProperty("SERVER_ADDR"));
        // int serverPort = Integer.parseInt(propReader.getProperty("SERVER_PORT"));
        // this.serverInfo = new NodeInfo(serverAddr, serverPort, "SERVER");

        // Prints local nodeInfo so you can tell people what to JOIN you with.
        System.out.println(nodeInfo);
    }

    // Runs Peer in a thread.
    private void run() {
        new Thread(new ClientThread(this.nodeInfo, listenSocket)).start();
    }

    // Basic setup to handle args + fallback for if the user enters no name.
    public static void main(String[] args) {
        String name = "Anonymous"; // Default name.
        if (args.length > 0) {
            // If the user entered a name, use that.
            name = args[0];
        }
        try {
            // Create Peer object using the name and begin running chat client.
            Peer peer = new Peer(name);
            peer.run();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}