package App;

import Records.Message;
import Records.MessageType;
import Records.NodeInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;

public class ListenThread extends Thread {
    ServerSocket listenSocket = null;

    public ListenThread(ServerSocket listenSocket) {
        this.listenSocket = listenSocket;
    }

    private void handleMessage(Message messageFromClient){
        Message propMessage = null;
        NodeInfo nodeInfo = null;
        String propText = null;
        String clientName = null;

        switch (messageFromClient.type()) {
            case JOIN -> {
                nodeInfo = (NodeInfo) messageFromClient.contents();
                try {
                    Socket socket = new Socket(nodeInfo.address(), nodeInfo.port());
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    out.writeObject((ArrayList<NodeInfo>)Peer.nodeList);
                    out.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Puts client info into arraylist and starts a server thread for that client.
                clientName = nodeInfo.name();
                Peer.nodeList.add(nodeInfo);
                // Craft join message.
                propText = "'" + clientName + "' joined chat.";
            }
            case JOIN_INFO -> {
                // Puts client info into arraylist and starts a server thread for that client.
                nodeInfo = (NodeInfo) messageFromClient.contents();
                clientName = nodeInfo.name();
                Peer.nodeList.add(nodeInfo);
                // Craft join message.
                propText = "'" + clientName + "' joined chat.";
            }
            case LEAVE -> {
                // Removes client from arraylist.
                nodeInfo = (NodeInfo) messageFromClient.contents();
                clientName = nodeInfo.name();
                Peer.nodeList.remove(nodeInfo);
                // Craft leave message.
                propText = "'" + clientName + "' left chat.";
            }
            case NOTES -> {
                // Formats and propagates text from client messages.
                String text = (String) messageFromClient.contents();
                // Craft message.
                propText = text;
            }
        }
        System.out.println(propText);
    }

    public void run(){
        ObjectInputStream fromPeer = null;
        Message messageFromPeer = null;
        // Talk to the client.
        // Socket loop.
        while (true) {
            try {
                // From socket should listen waiting for connection, then on accept open object stream, then read message.
                // Close object stream, and create thread to do sending to peers and opens socket again.
                // Create and open socket.
                Socket socket = listenSocket.accept();
                fromPeer = new ObjectInputStream(socket.getInputStream());
                messageFromPeer = (Message) fromPeer.readObject();
                handleMessage(messageFromPeer);
                // System.out.println("DEBUG: Received message " + messageFromClient);
                fromPeer.close();
                socket.close();
            } catch (Exception e) {
                System.err.println("Error reading character from client.");
                return;
            }
        }
    }
}