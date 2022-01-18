package App;

import Records.Message;
import Records.MessageType;
import Records.NodeInfo;

import java.io.ObjectInputStream;
import java.io.DataOutputStream;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Server extends Thread{
    Socket clientSocket = null;
    String clientName = null;

    public Server(Socket clientSocket) {
        this.clientSocket = clientSocket;
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

    public void run() {
        ObjectInputStream fromClient = null;
        ObjectOutputStream toClient = null;

        Message messageFromClient = null;
        boolean keepGoing = true;

        // Variables for message propagation.
        String propText;
        Message propMessage;

        // First set up the streams.
        try {
            fromClient = new ObjectInputStream(clientSocket.getInputStream());
            toClient = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Error opening network streams (Server).");
            return;
        }

        // Talk to the client.
        while (keepGoing) {
            try {
                messageFromClient = (Message) fromClient.readObject();
                NodeInfo nodeInfo = null;

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
                        ServerRun.nodeList.put(nodeInfo, clientSocket);
                        // Craft join message.
                        String propText = clientName + " joined chat.";
                        Message propMessage = new Message(MessageType.NOTES, propText);
                        // Propagate join message.
                        for(Socket propSocket : ServerRun.nodeList.values()) {
                            // Propagation stream opens and closes each time since you can't change sockets.
                            ObjectOutputStream propStream = new ObjectOutputStream(propSocket.getOutputStream());
                            propStream.writeObject(clientName + " joined chat.");
                            propStream.close();
                        }
                        break;
                    case LEAVE: // Removes client from hashtable.
                        nodeInfo = (NodeInfo) messageFromClient.contents();
                        ServerRun.nodeList.remove(nodeInfo);
                        // Propagate leave message.
                        for(Socket propSocket : ServerRun.nodeList.values()) {
                            // Propagation stream opens and closes each time since you can't change sockets.
                            ObjectOutputStream propStream = new ObjectOutputStream(propSocket.getOutputStream());
                            propStream.writeObject(clientName + " left chat.");
                            propStream.close();
                        }
                        break;
                    case NOTES: // Formats and propagates text from client messages.
                        String text = (String) messageFromClient.contents();
                        // Propagate message.
                        for(Socket propSocket : ServerRun.nodeList.values()) {
                            // Propagation stream opens and closes each time since you can't change sockets.
                            ObjectOutputStream propStream = new ObjectOutputStream(propSocket.getOutputStream());
                            propStream.writeObject("#" + clientName + ": " + text);
                            propStream.close();
                        }
                        toClient.writeObject("#" + clientName + ": " + text); //wrap in message object
                        break;
                }
                // System.out.print(messageFromClient);
            } catch (Exception e) {
                System.err.println("Error reading character from client");
                return;
            }
        }

        try {
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing socket to client");
        }

    }

    // public static void main(String[] args) {

    // }
}

//TODO write comments