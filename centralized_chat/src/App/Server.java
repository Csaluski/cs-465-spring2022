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
    Socket listenSocket = null;
    String clientName = null;

    public Server(Socket listenSocket) {
        this.listenSocket = listenSocket;
    }

    // TODO change to ephemeral sockets and data streams
    //Sends message via ObjectOutputStream (since we want to send the whole Message object).
    private void sendMessage(Message propMessage) {
        System.out.println(propMessage);
        for(Socket propSocket : ServerRun.nodeList.values()) {
            // Propagation stream opens and closes each time since you can't change sockets.
            ObjectOutputStream propStream = new ObjectOutputStream(propSocket.getOutputStream());
            propStream.writeObject(propMessage);
            propStream.close();
        }
    }
    
    private Message createMessage(Message messageFromClient) {
        Message propMessage = null;
        NodeInfo nodeInfo = null;
        String propText;
        switch(messageFromClient.type()) {
            case JOIN: // Puts client info into hashtable and starts a server thread for that client.
                nodeInfo = (NodeInfo) messageFromClient.contents();
                clientName = nodeInfo.name();
                ServerRun.nodeList.put(nodeInfo, listenSocket);
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
        return propMessage;
    }

    // private Thread listenThread() {
    //     // from socket should listen waiting for connection, then on accept open object stream, then read message
    //     // close object stream, and create thread to do sending to peers and opens socket again.
    //     // create and open socket

    //     // run thread that does socket loop

    //     // socket loop
    //     // hold listen socket open
    //     // accept and open object stream
    //     // read Message object
    //     Message clientMessage;
    //     // close stream
    //     // create sendThread with message and run
    //     Thread sendThread = sendThread(clientMessage);
    //     sendThread.run();
    //     // reopen listen socket

    // }

    private Thread sendThread(Message message) {
        Message propMessage = createMessage(message);
        if(propMessage != null) {
            sendMessage(propMessage);
        }
    }

    // sets up listen thread, then listen thread spawns send thread when it has message to send
    public void run() {
        ObjectInputStream fromClient = null;
        ObjectOutputStream toClient = null;

        Message messageFromClient = null;
        boolean keepGoing = true;

        // Variables for message propagation.
        // Message propMessage;

        // Thread listenThread;
        // First set up the streams.
        try {
            // listenThread = listenThread();
            // listenThread.run();
            fromClient = new ObjectInputStream(listenSocket.getInputStream());
            toClient = new ObjectOutputStream(listenSocket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Error opening network streams (Server).");
            return;
        }

        // Talk to the client.
        while (keepGoing) {
            try {
                messageFromClient = (Message) fromClient.readObject();
                if(messageFromClient.type() == SHUTDOWN){ // Prints out on the server when a client shuts down for readability, kills thread.
                    System.out.println(clientName + " SHUTDOWN");
                    keepGoing = false;
                }else{
                    sendThread(messageFromClient);
                }
                // TODO break this out into a function
                // Message handler.
            } catch (Exception e) {
                System.err.println("Error reading character from client");
                return;
            }
        }

        // Close up shop when everything is done.
        try {
            listenSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing socket to client");
        }

    }

    // public static void main(String[] args) {

    // }
}

//TODO write comments