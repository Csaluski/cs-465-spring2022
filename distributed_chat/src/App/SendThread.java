package App;

import Records.NodeInfo;
import Records.MessageType;
import Records.Message;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

import java.net.Inet4Address;
import java.net.Socket;

import java.util.Scanner;

public class SendThread extends Thread {
    private boolean connected;
    private final NodeInfo nodeInfo;

    SendThread(NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    private Message createMessage(String[] rawMessage) {
        return switch (rawMessage[0]) {
            case "JOIN" -> new Message(MessageType.JOIN, nodeInfo);
            case "LEAVE" -> new Message(MessageType.LEAVE, nodeInfo);
            case "SHUTDOWN" -> new Message(MessageType.SHUTDOWN, nodeInfo);
            default -> new Message(MessageType.NOTES, nodeInfo.name() + ": " + rawMessage[0]); // Formats with client nickname.
        };
    }

    private void joinChat(Message newMessage, String[] messages) {
        try {
            int port = Integer.parseInt(messages[1]);
            Inet4Address address = (Inet4Address) Inet4Address.getByName(messages[2]);
            Socket socket = new Socket(address, port);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(newMessage);
            out.close();
            socket.close();
            connected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(Message newMessage) {

    }

    public void run(){
        Scanner input = new Scanner(System.in);
        while (true) {
            // Read the message to deliver.
            String message = input.nextLine();
            String[] messages = message.split(" ");
            Message newMessage = createMessage(messages);

            // Special case logic.
            // Manage connection flag.
            // assume sending join message will succeed
            if (!connected && newMessage.type() == MessageType.JOIN) {
                joinChat(newMessage, messages);
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
    }
    
}