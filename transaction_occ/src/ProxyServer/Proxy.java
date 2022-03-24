package ProxyServer;

import Records.OpMessage;

import java.net.Socket;

// Creates message of type OPEN_TRANSACTION
// opens connection to transaction server & sends message
// (leaves connection open)
public class Proxy {
    // Send messages to transaction server through this socket
    private final Socket clientSocket = null;

    Proxy(){
        PropertyHandler propReader = new PropertyHandler("server.properties");
        Inet4Address serverAddr = (Inet4Address) Inet4Address.getByName(propReader.getProperty("SERVER_ADDR"));
        int serverPort = Integer.parseInt(propReader.getProperty("SERVER_PORT"));
        clientSocket = new Socket(serverAddr, serverPort);
    }

    // private OpMessage makeMessage(OpMessageType){

    //     return null;
    // }

    // return transaction ID
    // connect to App.Server.Server by using server port and ip
    // get transaction ID from transaction manager via stream
    private void openTransaction(){
        OpMessage openMessage = new OpMessage(OpMessageType.OPEN_TRANSACTION, null);
        sendMessage(openMessage);
        receiveMessage();
    }

    private void closeTransaction(){
        OpMessage closeMessage = new OpMessage(OpMessageType.CLOSE_TRANSACTION, null);
        sendMessage(closeMessage);
        receiveMessage();
    }

    // Send read request to transaction worker through socket
    private void read(int accountID){
        OpMessage readMessage = new OpMessage(OpMessageType.READ, accountID);
        sendMessage(readMessage);
        receiveMessage();
    }

    // Send write request to transaction worker through socket
    private void write(int accountID, int amount){
        OpMessage writeMessage = new OpMessage(OpMessageType.WRITE, new AccountRecord(accountID, amount));
        sendMessage(writeMessage);
        receiveMessage();
    }

    // Handle message sending based on previous code.
    private void sendMessage(OpMessage opMessage){
        try {
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.writeObject(opMessage);
            System.out.println("DEBUG: " + opMessage.toString());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveMessage(){
        try {
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            ResponseMessage responseMessage = (ResponseMessage) in.readObject();
            System.out.println("DEBUG: " + responseMessage.toString());
            in.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}