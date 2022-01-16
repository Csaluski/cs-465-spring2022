import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.io.IOException;
import java.net.Socket;

public class ReceivingText extends Thread{
    Socket serverSocket = null;

    public ReceivingText(Socket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void run() {
        DataInputStream fromClient = null;
        String text = null;

        // first get the streams
        try {
            fromClient = new DataInputStream(serverSocket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error opening network streams");
            System.out.println(e);
            return;
        }

        // now talk to the client
        while (!serverSocket.isClosed()) {
            System.out.println("waiting text");
            try {
                text = fromClient.readLine();
                System.out.println(text);
            } catch (Exception e) {
                System.err.println("Error reading character from client");
                return;
            }
        }
        try{
            fromClient.close();
        } catch (Exception e) {
            System.err.println("Error reading character from client");
            return;
        }
    
        System.out.println("Thread closed");
    }
}
