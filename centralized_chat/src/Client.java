import java.io.DataInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Socket;

import java.util.Scanner;

public class Client {

    private final NodeInfo nodeInfo;
    private final NodeInfo serverInfo;
    // private final ServerSocket listenSocket;
    private Socket socket = null;
    private Scanner input = null;
    private DataInputStream fromServer = null;
    private ObjectOutputStream out = null;

    // TODO figure out how to get local IP in a real way
    private Client(String name) throws IOException {
        
        Inet4Address address = (Inet4Address) Inet4Address.getByName("localhost");
        int port = 23657;
        try {
            socket = new Socket(address, port);
            fromServer = new DataInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch(IOException i) {
            System.out.println(i);
        }
        this.nodeInfo = new NodeInfo(address, port, name);
        this.serverInfo = new NodeInfo(address, 23467, "SERVER");
        // new Thread(new ReceivingText(socket)).start();
        input = new Scanner(System.in);
    }

    private Message createMessage(String rawMessage) {
        return switch (rawMessage) {
            case "JOIN"  -> new Message(MessageType.JOIN, nodeInfo);
            case "LEAVE" -> new Message(MessageType.LEAVE, nodeInfo);
            default      -> new Message(MessageType.NOTES, rawMessage);
        };
    }

    private void run() {
        Thread sendMessage = new Thread(new Runnable() 
        {
            @Override
            public void run() {
                while (!socket.isClosed()) {
  
                    // read the message to deliver.
                    String msg = input.nextLine();
                      
                    try {
                        // write on the output stream
                        Message newMessage = createMessage(msg); 
                        out.writeObject(newMessage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
          
        // readMessage thread
        Thread readMessage = new Thread(new Runnable() 
        {
            @Override
            public void run() {
                while (!socket.isClosed()) {
                    try {
                        // read the message sent to this client
                        String msg = fromServer.readUTF();
                        if(msg == "SHOTDOWN_ALL") {
                            System.out.println("SHOTDOWN_ALL");
                            try {
                                socket.close();
                            } catch(IOException i) {
                                System.out.println(i);
                            }
                        }
                        System.out.println(msg);
                    } catch (IOException e) {
  
                        e.printStackTrace();
                    }
                }
            }
        });
        sendMessage.start();
        readMessage.start();
    }

    public static void main(String[] args) {
        String name = "Anonymous";
        if(args[0] != null) {
            name = args[0];
        }
        try {
            Client client = new Client(name);
            client.run();
        } catch(IOException i) {
            System.out.println(i);
        }   
    }
}
