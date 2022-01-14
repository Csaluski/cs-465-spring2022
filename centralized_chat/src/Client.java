import java.io.DataInputStream;
import java.io.ObjectOutputStream;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Socket;

public class Client {

    private final NodeInfo nodeInfo;
    private final NodeInfo serverInfo;
    // private final ServerSocket listenSocket;
    private Socket socket = null;
    private DataInputStream input = null;
    private ObjectOutputStream out = null;

    // TODO figure out how to get local IP in a real way
    private Client(String name) throws IOException {
        Inet4Address address = (Inet4Address) Inet4Address.getByName("localhost");
        int port = 23657;
        try {
            socket = new Socket(address, port);
            input = new DataInputStream(System.in);
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch(IOException i) {
            System.out.println(i);
        }
        this.nodeInfo = new NodeInfo(address, port, name);
        this.serverInfo = new NodeInfo(address, 23467, "SERVER");
        new Thread(new ReceivingText(socket)).start();
    }

    private Message createMessage(String rawMessage) {
        return switch (rawMessage) {
            case "JOIN"  -> new Message(MessageType.JOIN, nodeInfo);
            case "LEAVE" -> new Message(MessageType.LEAVE, nodeInfo);
            default      -> new Message(MessageType.NOTES, rawMessage);
        };
    }

    private void run() {
        String line = "";
        while (!line.equals("LEAVE")) {
            try {
                line = input.readLine();
                Message newMessage = createMessage(line); 
                out.writeObject(newMessage);
            } catch(IOException i) {
                System.out.println(i);
            }
        }
        try {
            input.close();
            out.close();
            socket.close();
        } catch(IOException i) {
            System.out.println(i);
        }
    }

    public static void main(String[] args) {
        try {
            Client client = new Client("test");
            client.run();
        } catch(IOException i) {
            System.out.println(i);
        }
        
    }

}
