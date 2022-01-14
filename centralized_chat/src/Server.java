import java.io.ObjectInputStream;
import java.io.DataOutputStream;

import java.io.IOException;
import java.net.Socket;

public class Server extends Thread{
    Socket clientSocket = null;

    public Server(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    private void sendMessage(String message) {
        for(int i=0;i<ServerThread.nodeList.size();i++) {
            try {
                Socket socket = new Socket(ServerThread.nodeList.get(i).getAddress(), ServerThread.nodeList.get(i).getPort());
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
        int state = 0;
        boolean keepGoing = true;

        // first get the streams
        try {
            fromClient = new ObjectInputStream(clientSocket.getInputStream());
            toClient = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Error opening network streams");
            return;
        }

        // now talk to the client
        while (keepGoing) {
            try {
                messageFromClient = (Message) fromClient.readObject();
                switch(messageFromClient.type()) {
                    case JOIN:
                        System.out.println("Joined");
                        ServerThread.nodeList.add((NodeInfo) messageFromClient.contents());
                        break;
                    case LEAVE:
                        System.out.println("Left");
                        ServerThread.nodeList.remove((NodeInfo) messageFromClient.contents());
                        keepGoing = false;
                        break;
                    case NOTES:
                        System.out.println("Notes");
                        sendMessage((String)messageFromClient.contents());
                        break;
                }
                // System.out.print(messageFromClient);
            } catch (Exception e) {
                System.err.println("Error reading character from client");
                return;
            }

            // try {
            //     toClient.writeByte('a');
            // } catch (IOException e) {
            //     System.err.println("Error writing character to client");
            //     return;            
            // }
            
            // if (charFromClient == 'q') {
            //     System.out.println("\nBailing out!");
            //     keepGoing = false;
            // }
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
