package ProxyServer;

import Records.*;
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
    private ObjectInputStream fromServer;
    private ObjectOutputStream toServer;
    private int transactionNumber = -1;

    // Proxy constructor, sets up proxy server to be used by the client
    public Proxy() {
        try {
            PropertyHandler propReader = new PropertyHandler("./config/Client.properties");
            Inet4Address serverAddr = (Inet4Address) Inet4Address.getByName(propReader.getProperty("SERVER_ADDR"));
            int serverPort = Integer.parseInt(propReader.getProperty("SERVER_PORT"));
            clientSocket = new Socket(serverAddr, serverPort);
            fromServer = new ObjectInputStream(clientSocket.getInputStream());
            toServer = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (Exception e) {
            System.out.println(e);
            return;
        }

    }

    // return transaction ID
    // connect to App.Server.Server by using server port and ip
    // get transaction ID from transaction manager via stream
    public int openTransaction() {
        OpMessage openMessage = new OpMessage(OpMessageType.OPEN_TRANSACTION, null);
        sendMessage(openMessage);
        ResponseMessage responseMessage = receiveMessage();
        if (responseMessage != null) {
            transactionNumber = (int) responseMessage.contents();
        }
        return transactionNumber;
    }

    // construct and send close message, receive response
    // check response, return success as boolean
    public boolean closeTransaction() {
        boolean success = false;
        OpMessage closeMessage = new OpMessage(OpMessageType.CLOSE_TRANSACTION, null);
        sendMessage(closeMessage);
        ResponseMessage responseMessage = receiveMessage();
        if (responseMessage != null && responseMessage.type() == ResponseMessageType.SUCCESS) {
            success = true;
        }
        return success;
    }

    // Send read request to transaction worker through socket
    public Account read(int accountID) {
        OpMessage readMessage = new OpMessage(OpMessageType.READ, accountID);
        sendMessage(readMessage);
        ResponseMessage responseMessage = receiveMessage();
        if (responseMessage != null) {
            return (Account)responseMessage.contents();
        }
        return null;
    }

    // Send write request to transaction worker through socket
    public Account write(int accountID, int amount) {
        OpMessage writeMessage = new OpMessage(OpMessageType.WRITE, new Account(accountID, amount));
        sendMessage(writeMessage);
        ResponseMessage responseMessage = receiveMessage();
        if (responseMessage != null) {
            return (Account)responseMessage.contents();
        }
        return null;
    }

    // Handle message sending based on previous code
    private void sendMessage(OpMessage opMessage) {
        try {
            toServer.writeObject(opMessage);
            System.out.println("DEBUG: Transaction #" + transactionNumber + " conducting " + opMessage.toString());
            // toServer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Receive a response message from the server and parse it, output for debug
    private ResponseMessage receiveMessage() {
        ResponseMessage responseMessage = null;
        try {
            responseMessage = (ResponseMessage) fromServer.readObject();
            System.out.println("DEBUG: " + responseMessage.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseMessage;
    }
}