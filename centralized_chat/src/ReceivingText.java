import java.io.ObjectInputStream;
import java.io.DataOutputStream;

import java.io.IOException;
import java.net.Socket;

public class ReceivingText extends Thread{
    Socket serverSocket = null;

    public ReceivingText(Socket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void run() {
        ObjectInputStream fromClient = null;
        DataOutputStream toClient = null;

        String text = null;

        // first get the streams
        try {
            fromClient = new ObjectInputStream(serverSocket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error opening network streams");
            return;
        }

        // now talk to the client
        while (!serverSocket.isClosed()) {
            try {
                text = fromClient.readLine();
                System.out.print(text);
            } catch (Exception e) {
                System.err.println("Error reading character from client");
                return;
            }
        }
    }
}
