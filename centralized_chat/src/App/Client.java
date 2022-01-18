
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

    private Client(String name) throws IOException {
        Inet4Address address = (Inet4Address) Inet4Address.getByName("localhost");
        int listenPort = -1;
        // Opens a listening port on an unused port, then takes the port number to use later in the socket loop
        try (ServerSocket socket = new ServerSocket(0)) {
            listenPort = socket.getLocalPort();
            socket.close();
        } catch (IOException e) {
            System.out.println(e);
        }
//        try {
//            socket = new Socket(address, port);
//            fromServer = new DataInputStream(socket.getInputStream());
//            out = new ObjectOutputStream(socket.getOutputStream());
//        } catch (IOException e) {
//            System.out.println(e);
//        }

        this.nodeInfo = new NodeInfo(address, listenPort, name);

        PropertyHandler propReader = new PropertyHandler("server.properties");

        Inet4Address serverAddr = (Inet4Address) Inet4Address.getByName(propReader.getProperty("serverAddr"));
        int serverPort = Integer.parseInt(propReader.getProperty("serverPort"));

        this.serverInfo = new NodeInfo(serverAddr, serverPort, "SERVER");

        input = new Scanner(System.in);
    }

    private Message createMessage(String rawMessage) {
        return switch (rawMessage) {
            case "JOIN" -> new Message(MessageType.JOIN, nodeInfo);
            case "LEAVE" -> new Message(MessageType.LEAVE, nodeInfo);
            case "SHUTDOWN" -> new Message(MessageType.SHUTDOWN, nodeInfo);
            default -> new Message(MessageType.NOTES, rawMessage);
        };
    }

    private void run() {
        // Thread that reads inputs, creates, and sends messages to server
        Thread sendMessage = new Thread(() -> {
            while (true) {
                // read the message to deliver.
                String msg = input.nextLine();
                Message newMessage = createMessage(msg);

                // TODO implement join/leave/shutdown logic, ie no double joining/sending messages when left, etc
                // TODO this should be implemented in server logic, but here we trust the client

                // try to connect to server from stored info, if it fails, it fails
                if (newMessage != null) {
                    try {
                        Socket socket = new Socket(serverInfo.address(), serverInfo.port());
                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                        out.writeObject(newMessage);
                        out.close();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // readMessage thread
        // Thread that receives messages from server and displays output to user
        Thread readMessage = new Thread(() -> {
            while (true) {
                try {
                    ServerSocket listenSocket = new ServerSocket(nodeInfo.port());
                    Socket listen = listenSocket.accept();
                    ObjectInputStream in = new ObjectInputStream(listen.getInputStream());

                    Message message = (Message) in.readObject();
                    showMessage(message);

                    in.close();
                    listen.close();
                    listenSocket.close();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        sendMessage.start();
        readMessage.start();
    }

    // client will only ever receive NOTES type messages from server,
    // other messages are sent with info for the sake of the server
    void showMessage(Message message) {
        System.out.println(message.contents());
    }

    public static void main(String[] args) {
        String name = "Anonymous";
        if (args.length > 1) {
            name = args[1];
        }
        try {
            Client client = new Client(name);
            client.run();
        } catch (IOException i) {
            System.out.println(i);
        }
    }
}

//TODO write comments