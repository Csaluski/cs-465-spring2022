package App;

import Records.NodeInfo;
import Records.MessageType;
import Records.Message;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

import java.net.*;

import java.util.Scanner;
import java.util.ArrayList;

public class ClientThread extends Thread {
    public static boolean connected = false;
    private final NodeInfo myNodeInfo;
    private final ServerSocket listenSocket;

    ClientThread(NodeInfo nodeInfo, ServerSocket listenSocket) {
        this.myNodeInfo = nodeInfo;
        this.listenSocket = listenSocket;
        new Thread(new ListenThread(listenSocket)).start();

    }

    private Message createMessage(String[] rawMessage) {
        return switch (rawMessage[0]) {
            case "JOIN" -> new Message(MessageType.JOIN, myNodeInfo);
            case "LEAVE" -> new Message(MessageType.LEAVE, myNodeInfo);
            case "SHUTDOWN" -> new Message(MessageType.SHUTDOWN, myNodeInfo);
            default -> new Message(MessageType.NOTES, myNodeInfo.name() + ": " + rawMessage[0]); // Formats with client nickname.
        };
    }

    private InetSocketAddress createDestination(String[] dest) {
        InetSocketAddress destAddr = null;
        try {
            destAddr = new InetSocketAddress(InetAddress.getByName(dest[1]), Integer.parseInt(dest[2]));
        } catch (Exception e) {
            System.out.println(e);
            destAddr = new InetSocketAddress("localhost", 0);
        }
        return destAddr;
    }

    @SuppressWarnings("unchecked")
    private void joinChat(Message newMessage, String[] messageWords) {
        try {
            int port = Integer.parseInt(messageWords[2]);
            Inet4Address address = (Inet4Address) Inet4Address.getByName(messageWords[1]);
            Socket socket = new Socket(address, port);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            out.writeObject(newMessage);
            Peer.nodeList = (ArrayList<NodeInfo>) in.readObject();
            sendJoinInfo();
            // add self to the peer nodelist
            Peer.nodeList.add(new NodeInfo(address, port, myNodeInfo.name()));

            in.close();
            out.close();
            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void sendJoinInfo() {
        try {
            for (NodeInfo nodeInfo : Peer.nodeList) {
                Socket socket = new Socket(nodeInfo.address(), nodeInfo.port());
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                Message joinInfo = new Message(MessageType.JOIN_INFO, this.myNodeInfo);
                out.writeObject(joinInfo);
                out.close();
                socket.close();
            }
            connected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("You joined the chat!");
    }

    private void sendMessage(Message newMessage) {
        new Thread(new SendThread(newMessage)).start();
    }

    public void run() {
        Scanner input = new Scanner(System.in);
        while (true) {
            // Read the message to deliver.
            String message = input.nextLine();
            String[] messageWords = message.split(" ");
            Message newMessage = createMessage(messageWords);
            // Special case logic.
            // Manage connection flag.
            // assume sending join message will succeed
            if (!connected && newMessage.type() == MessageType.JOIN) {
                if (messageWords.length != 3) {
                    System.out.println("Must also specify peer IP and port");
                }
                else {
                    joinChat(newMessage, messageWords);
                }
                continue;
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
            } else if (!connected && newMessage.type() == MessageType.NOTES) {
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
                newMessage = new Message(MessageType.LEAVE, myNodeInfo);
                sendMessage(newMessage);
                System.exit(0);
            }
            // Otherwise, message is good to send.
            sendMessage(newMessage);
        }
    }

}