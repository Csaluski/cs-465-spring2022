package App;

import java.io.*;

import PropertyHandler.PropertyHandler;
import Records.Message;
import Records.MessageType;
import Records.NodeInfo;

import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final NodeInfo nodeInfo;
    private final NodeInfo serverInfo;
    private Scanner input = null;
    private boolean connected;
    private final ServerSocket listenSocket;

    // Client constructor and initialization.
    private Client(String name) throws IOException {
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

        // From here onward is the initialization process.
        listenSocket = listenSocketTemp;
        this.nodeInfo = new NodeInfo(address, listenPort, name);

        PropertyHandler propReader = new PropertyHandler("server.properties");

        Inet4Address serverAddr = (Inet4Address) Inet4Address.getByName(propReader.getProperty("SERVER_ADDR"));
        int serverPort = Integer.parseInt(propReader.getProperty("SERVER_PORT"));

        this.serverInfo = new NodeInfo(serverAddr, serverPort, "SERVER");
        System.out.println("Client ready to go, connecting to " + serverInfo);

        input = new Scanner(System.in);
    }

    // Message creation using Message and MessageType.
    private Message createMessage(String rawMessage) {
        return switch (rawMessage) {
            case "JOIN" -> new Message(MessageType.JOIN, nodeInfo);
            case "LEAVE" -> new Message(MessageType.LEAVE, nodeInfo);
            case "SHUTDOWN" -> new Message(MessageType.SHUTDOWN, nodeInfo);
            default -> new Message(MessageType.NOTES, nodeInfo.name() + ": " + rawMessage); // Formats with client nickname.
        };
    }

    // Message sending through ObjectOutputStream.
    private void sendMessage(Message message) {
        // Try to connect to server from stored info, if it fails, it fails.
        try {
            Socket socket = new Socket(serverInfo.address(), serverInfo.port());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(message);
            System.out.println("DEBUG: " + message.toString());
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Thread that reads inputs, creates, and sends messages to server.
    private Thread sendThread() {
        Thread thread = new Thread(() -> {
            while (true) {
                // Read the message to deliver.
                String msg = input.nextLine();
                Message newMessage = createMessage(msg);

                // Special case logic.
                // Manage connection flag.
                // assume sending join message will succeed
                if (!connected && newMessage.type() == MessageType.JOIN) {
                    connected = true;
                }
                // assume sending leave message will succeed
                else if (connected && newMessage.type() == MessageType.LEAVE) {
                    connected = false;
                }
                // Don't allow double joining a server.
                else if (connected && newMessage.type() == MessageType.JOIN) {
                    System.out.println("You are already joined!");
                    continue;
                }
                // Don't allow disconnecting from an already disconnected server.
                else if (!connected && newMessage.type() == MessageType.LEAVE) {
                    System.out.println("You are not connected, cannot leave.");
                    continue;
                }
                else if (!connected && newMessage.type() == MessageType.NOTES)
                {
                    System.out.println("You are not connected, cannot send message.");
                    continue;
                }
                // If already disconnected, simply kill client.
                else if (!connected && newMessage.type() == MessageType.SHUTDOWN) {
                    System.out.println("Shutting down.");
                    System.exit(0);
                }
                // If connected and shutting down, create leave message and then kill client.
                else if (connected && newMessage.type() == MessageType.SHUTDOWN) {
                    System.out.println("Disconnecting and shutting down.");
                    newMessage = new Message(MessageType.LEAVE, nodeInfo);
                    sendMessage(newMessage);
                    System.exit(0);
                }
                // Otherwise, message is good to send.
                sendMessage(newMessage);
            }
        });

        return thread;
    }

    // Thread that receives messages from server and displays output to user.
    private Thread readThread() {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    // Wait for server to initialize connection using callback information provided in nodeInfo.
                    Socket listen = listenSocket.accept();

                    // Create object in stream from socket and read message object from server.
                    ObjectInputStream in = new ObjectInputStream(listen.getInputStream());
                    Message message = (Message) in.readObject();
                    showMessage(message);

                    // Clean up socket and stream after read completes.
                    in.close();
                    listen.close();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        return thread;
    }

    // Start sending and reading threads for the client.
    private void run() {
        Thread sendThread = sendThread();
        Thread readThread = readThread();
        sendThread.start();
        readThread.start();
    }

    // Client will only ever receive NOTES type messages from server, other messages are sent with info for the sake of the server.
    // This method will be updated considerably in the future P2P implementation of the client.
    void showMessage(Message message) {
        System.out.println(message.contents());
    }

    // Tracks the client name and initializes the client on start.
    public static void main(String[] args) {
        String name = "Anonymous";
        if (args.length > 1) {
            name = args[1];
        }
        try {
            Client client = new Client(name);
            client.run();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}