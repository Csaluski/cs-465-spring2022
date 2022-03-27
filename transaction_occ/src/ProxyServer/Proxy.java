package ProxyServer;

import Records.OpMessage;
import Records.OpMessageType;
import Records.Account;
import Records.ResponseMessage;

import PropertyHandler.PropertyHandler;

import java.net.Inet4Address;
import java.net.Socket;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

// Creates message of type OPEN_TRANSACTION
// opens connection to transaction server & sends message
// (leaves connection open)
public class Proxy {
    // Send messages to transaction server through this socket
    private Socket clientSocket;
    private int transactionID = -1;

    public Proxy(){
        try {
            PropertyHandler propReader = new PropertyHandler("server.properties");
            Inet4Address serverAddr = (Inet4Address) Inet4Address.getByName(propReader.getProperty("SERVER_ADDR"));
            int serverPort = Integer.parseInt(propReader.getProperty("SERVER_PORT"));
            clientSocket = new Socket(serverAddr, serverPort);
        } catch (Exception e) {
            System.out.println(e);
            return;
        }
        
    }

    // private OpMessage makeMessage(OpMessageType){

    //     return null;
    // }

    // return transaction ID
    // connect to App.Server.Server by using server port and ip
    // get transaction ID from transaction manager via stream
    public void openTransaction(){
        OpMessage openMessage = new OpMessage(OpMessageType.OPEN_TRANSACTION, null);
        sendMessage(openMessage);
        receiveMessage();
    }

    public void closeTransaction(){
        OpMessage closeMessage = new OpMessage(OpMessageType.CLOSE_TRANSACTION, null);
        sendMessage(closeMessage);
        receiveMessage();
    }

    // Send read request to transaction worker through socket
    public void read(int accountID){
        OpMessage readMessage = new OpMessage(OpMessageType.READ, accountID);
        sendMessage(readMessage);
        receiveMessage();
    }

    // Send write request to transaction worker through socket
    public void write(int accountID, int amount){
        OpMessage writeMessage = new OpMessage(OpMessageType.WRITE, new Account(accountID, amount));
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
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}