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
    String clientName = null;

    public Server(ServerSocket listenSocket) {
        this.listenSocket = listenSocket;
    }

    // TODO change to ephemeral sockets and data streams
    //Sends message via ObjectOutputStream (since we want to send the whole Message object).
    private void sendMessage(Message message) {
        System.out.println(message);
        for (NodeInfo key : ServerRun.nodeList.keySet()) {
            try {
                Socket socket = ServerRun.nodeList.get(key);
                //New output stream when a message needs to be sent.
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                //Send the message.
                out.writeObject(message);
            } catch(IOException e) {
                System.out.println("Could not send message.");
                System.out.println(e);
            }
        }
    }

    private Thread listenThread() {
        // from socket should listen waiting for connection, then on accept open object stream, then read message
        // close object stream, and create thread to do sending to peers and opens socket again.
        // create and open socket

        // run thread that does socket loop

        // socket loop
        // hold listen socket open
        // accept and open object stream
        // read Message object
        Message clientMessage;
        // close stream
        // create sendThread with message and run
        Thread sendThread = sendThread(clientMessage);
        sendThread.run();
        // reopen listen socket

    }

    private Thread sendThread(Message message) {

    }

    // sets up listen thread, then listen thread spawns send thread when it has message to send
    public void run() {
        ObjectInputStream fromClient = null;
        ObjectOutputStream toClient = null;

        Message messageFromClient = null;
        boolean keepGoing = true;

        // Variables for message propagation.
        String propText;
        Message propMessage;

        Thread listenThread;
        // First set up the streams.
        try {
            listenThread = listenThread();
            listenThread.run();

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
                NodeInfo nodeInfo = null;

                // TODO break this out into a function
                // Message handler.
                switch(messageFromClient.type()) {
                    case SHUTDOWN: // Prints out on the server when a client shuts down for readability, kills thread.
                        nodeInfo = (NodeInfo) messageFromClient.contents();
                        System.out.println(clientName + " SHUTDOWN");
                        keepGoing = false;
                        break;
//                    case SHUTDOWN_ALL:
//                        keepGoing = false;
//                        sendMessage("SHUTDOWN_ALL");
//                        break;
                    case JOIN: // Puts client info into hashtable and starts a server thread for that client.
                        nodeInfo = (NodeInfo) messageFromClient.contents();
                        clientName = nodeInfo.name();
                        ServerRun.nodeList.put(nodeInfo, listenSocket);
                        // Craft join message.
                        propText = clientName + " joined chat.";
                        propMessage = new Message(MessageType.NOTES, propText);  //NOTES type because textual content.
                        // Propagate join message.
                        for(Socket propSocket : ServerRun.nodeList.values()) {
                            // Propagation stream opens and closes each time since you can't change sockets.
                            ObjectOutputStream propStream = new ObjectOutputStream(propSocket.getOutputStream());
                            propStream.writeObject(propMessage);
                            propStream.close();
                        }
                        break;
                    case LEAVE: // Removes client from hashtable.
                        nodeInfo = (NodeInfo) messageFromClient.contents();
                        ServerRun.nodeList.remove(nodeInfo);
                        // Craft leave message.
                        propText = clientName + " left chat.";
                        propMessage = new Message(MessageType.NOTES, propText); //NOTES type because textual content.
                        // Propagate leave message.
                        for(Socket propSocket : ServerRun.nodeList.values()) {
                            // Propagation stream opens and closes each time since you can't change sockets.
                            ObjectOutputStream propStream = new ObjectOutputStream(propSocket.getOutputStream());
                            propStream.writeObject(propMessage);
                            propStream.close();
                        }
                        break;
                    case NOTES: // Formats and propagates text from client messages.
                        String text = (String) messageFromClient.contents();
                        // Craft message.
                        propText = "#" + clientName + ": " + text;
                        propMessage = new Message(MessageType.NOTES, propText);  //NOTES type because textual content.
                        // Propagate message.
                        for(Socket propSocket : ServerRun.nodeList.values()) {
                            // Propagation stream opens and closes each time since you can't change sockets.
                            ObjectOutputStream propStream = new ObjectOutputStream(propSocket.getOutputStream());
                            propStream.writeObject(propMessage);
                            propStream.close();
                        }
                        break;
                }
                System.out.print(messageFromClient);
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