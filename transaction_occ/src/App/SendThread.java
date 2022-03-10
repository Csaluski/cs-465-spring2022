package App;

import Records.Message;
import Records.NodeInfo;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;

// Handles connection part of sending messages.
public class SendThread extends Thread {
    private Message message;

    // Constructor.
    SendThread(Message message) {
        this.message = message;
    }

    // Connects and writes message.
    private void sendMessage() {
        for (NodeInfo nodeInfo : Peer.nodeList) {
            // Propagation stream opens and closes each time since you can't change sockets.
            try {
                // System.out.println("DEBUG: Sending message " + propMessage + " to client " + nodeInfo);
                Socket propSocket = new Socket(nodeInfo.address(), nodeInfo.port());
                ObjectOutputStream propStream = new ObjectOutputStream(propSocket.getOutputStream());
                propStream.writeObject(message);
                propStream.close();
                propSocket.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    // Running sends a message in this case.
    public void run(){
        sendMessage();
    }
}