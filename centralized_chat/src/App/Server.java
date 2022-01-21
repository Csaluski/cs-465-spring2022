package App;

import Records.Message;
import Records.MessageType;
import Records.NodeInfo;

import java.io.ObjectInputStream;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread{
    ServerSocket listenSocket = null;

    public Server(ServerSocket listenSocket) {
        this.listenSocket = listenSocket;
    }

    // TODO change to ephemeral sockets and data streams
    //Sends message via ObjectOutputStream (since we want to send the whole Message object).
    private void sendMessage(Message propMessage) {
        for(NodeInfo nodeInfo : ServerRun.nodeList) {
            // Propagation stream opens and closes each time since you can't change sockets.
            try {
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
        switch(messageFromClient.type()) {
            case SHUTDOWN:
                propText = clientName + " SHUTDOWN.";
                break;
            case JOIN: // Puts client info into hashtable and starts a server thread for that client.
                nodeInfo = (NodeInfo) messageFromClient.contents();
                clientName = nodeInfo.name();
                ServerRun.nodeList.add(nodeInfo);
                // Craft join message.
                propText = clientName + " joined chat.";
                propMessage = new Message(MessageType.NOTES, propText);  //NOTES type because textual content.
                break;
            case LEAVE: // Removes client from hashtable.
                nodeInfo = (NodeInfo) messageFromClient.contents();
                ServerRun.nodeList.remove(nodeInfo);
                // Craft leave message.
                propText = clientName + " left chat.";
                propMessage = new Message(MessageType.NOTES, propText); //NOTES type because textual content.
                break;
            case NOTES: // Formats and propagates text from client messages.
                String text = (String) messageFromClient.contents();
                // Craft message.
                propText = "#" + clientName + ": " + text;
                propMessage = new Message(MessageType.NOTES, propText);  //NOTES type because textual content.
                break;
        }
        // Prints out on the server when a client shuts down for readability
        System.out.println(propText);

        return propMessage;
    }

    private Thread sendThread(Message message) {
        Thread thread = new Thread(() -> {
            Message propMessage = createMessage(message);
            if(propMessage != null) {
                sendMessage(propMessage);
            }
        });
        return thread;
    }

    // sets up listen thread, then listen thread spawns send thread when it has message to send
    public void run() {
        ObjectInputStream fromClient = null;
        ObjectOutputStream toClient = null;
        Message messageFromClient = null;

        // Talk to the client.
        while (true) {
            try {
                Socket socket = listenSocket.accept();
                fromClient = new ObjectInputStream(socket.getInputStream());
                toClient = new ObjectOutputStream(socket.getOutputStream());
                messageFromClient = (Message) fromClient.readObject();
                sendThread(messageFromClient).run();
                // TODO break this out into a function
                // Message handler.
            } catch (Exception e) {
                System.err.println("Error reading character from client");
                return;
            }
        }

        // Close up shop when everything is done.
        // try {
        //     listenSocket.close();
        // } catch (IOException e) {
        //     System.err.println("Error closing socket to client");
        // }

    }
}

//TODO write comments