package App;

import Records.Message;
import Records.MessageType;
import Records.NodeInfo;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
    ServerSocket listenSocket = null;

    public Server(ServerSocket listenSocket) {
        this.listenSocket = listenSocket;
    }

    // Sends message via ObjectOutputStream (since we want to send the whole Message object).
    private void sendMessage(Message propMessage) {
        for (NodeInfo nodeInfo : ServerRun.nodeList) {
            // Propagation stream opens and closes each time since you can't change sockets.
            try {
                System.out.println("DEBUG: Sending message " + propMessage + " to client " + nodeInfo);
                Socket propSocket = new Socket(nodeInfo.address(), nodeInfo.port());
                ObjectOutputStream propStream = new ObjectOutputStream(propSocket.getOutputStream());
                propStream.writeObject(propMessage);
                propStream.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    private Message createMessage(Message messageFromClient) {
        Message propMessage = null;
        NodeInfo nodeInfo = null;
        String propText = null;
        String clientName = null;

        switch (messageFromClient.type()) {
            case JOIN -> {
                // Puts client info into arraylist and starts a server thread for that client.
                nodeInfo = (NodeInfo) messageFromClient.contents();
                clientName = nodeInfo.name();
                ServerRun.nodeList.add(nodeInfo);
                // Craft join message.
                propText = "'" + clientName + "' joined chat.";
                propMessage = new Message(MessageType.NOTES, propText);  //NOTES type because textual content.
            }
            case LEAVE -> {
                // Removes client from arraylist.
                nodeInfo = (NodeInfo) messageFromClient.contents();
                clientName = nodeInfo.name();
                ServerRun.nodeList.remove(nodeInfo);
                // Craft leave message.
                propText = "'" + clientName + "' left chat.";
                propMessage = new Message(MessageType.NOTES, propText); //NOTES type because textual content.
            }
            case NOTES -> {
                // Formats and propagates text from client messages.
                String text = (String) messageFromClient.contents();
                // Craft message.
                propText = text;
                propMessage = new Message(MessageType.NOTES, propText);  //NOTES type because textual content.
            }
        }
        // Prints out on the server when a client sends message for logging purposes.
        System.out.println(propText);

        return propMessage;
    }

    // Passes prepared messages along for propagation.
    private Thread sendThread(Message message) {
        Thread thread = new Thread(() -> {
            Message propMessage = createMessage(message);
            if (propMessage != null) {
                sendMessage(propMessage);
            }
        });
        return thread;
    }

    // Sets up listen thread, then listen thread spawns send thread when it has message to send.
    public void run() {
        ObjectInputStream fromClient = null;
        Message messageFromClient = null;

        // Talk to the client.
        // Socket loop.
        while (true) {
            try {
                // From socket should listen waiting for connection, then on accept open object stream, then read message.
                // Close object stream, and create thread to do sending to peers and opens socket again.
                // Create and open socket.
                Socket socket = listenSocket.accept();
                fromClient = new ObjectInputStream(socket.getInputStream());
                messageFromClient = (Message) fromClient.readObject();
                System.out.println("DEBUG: Received message " + messageFromClient);
                // Run thread that does socket loop.
                sendThread(messageFromClient).start();
                fromClient.close();
                socket.close();
            } catch (Exception e) {
                System.err.println("Error reading character from client.");
                return;
            }
        }

    }
}