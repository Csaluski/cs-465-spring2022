import java.io.ObjectInputStream;
import java.io.DataOutputStream;

import java.io.IOException;
import java.net.Socket;

public class Server extends Thread{
    Socket clientSocket = null;
    String clientName = null;

    public Server(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    private void sendMessage(String message) {
        System.out.println(message);
        for (NodeInfo key : ServerThread.nodeList.keySet()) {
            try {
                Socket socket = ServerThread.nodeList.get(key); 
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF(message);
            } catch(IOException e) {
                System.out.println("Could not send message");
                System.out.println(e);
            }
        }
    }

    public void run() {
        
        ObjectInputStream fromClient = null;
        DataOutputStream toClient = null;

        Message messageFromClient = null;
        boolean keepGoing = true;

        // first get the streams
        try {
            fromClient = new ObjectInputStream(clientSocket.getInputStream());
            toClient = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Error opening network streams (Server)");
            return;
        }

        // now talk to the client
        while (keepGoing) {
            try {
                messageFromClient = (Message) fromClient.readObject();
                NodeInfo nodeInfo = null;
                switch(messageFromClient.type()) {
                    case SHOTDOWN:
                        nodeInfo = (NodeInfo) messageFromClient.contents();
                        System.out.println(clientName + " SHOTDOWN");
                        keepGoing = false;
                        break;
                    case SHOTDOWN_ALL:
                        keepGoing = false;
                        sendMessage("SHOTDOWN_ALL");
                        break;
                    case JOIN:
                        nodeInfo = (NodeInfo) messageFromClient.contents();
                        clientName = nodeInfo.getName();
                        ServerThread.nodeList.put(nodeInfo, clientSocket);
                        sendMessage(clientName + " joined chat.");
                        break;
                    case LEAVE:
                        nodeInfo = (NodeInfo) messageFromClient.contents();
                        ServerThread.nodeList.remove(nodeInfo);
                        sendMessage(clientName + " left from chat.");
                        break;
                    case NOTES:
                        String text = (String) messageFromClient.contents();
                        sendMessage("#" + clientName + ": " + text);
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
