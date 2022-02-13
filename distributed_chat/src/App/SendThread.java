package App;

import Records.Message;
import Records.NodeInfo;

import java.io.ObjectOutputStream;
import java.io.IOException;

import java.net.Socket;

public class SendThread extends Thread {
    private Message message;

    SendThread(Message message) {
        this.message = message;
    }

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

    public void run(){
        sendMessage();
    }
}