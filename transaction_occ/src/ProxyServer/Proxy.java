package ProxyServer;

import Records.OpMessage;

import java.net.Socket;

public class Proxy {
    // Send messages to transaction server through this socket
    private final Socket clientSocket = null;

    Proxy(){

    }

    private OpMessage makeMessage(){
        return null;
    }

    // return transaction ID
    // connect to App.Server.Server by using server port and ip
    // get transaction ID from transaction manager via stream
    private int openTransaction(){
        return 0;
    }

    private void closeTransaction(){
        
    }

    // Send read request to transaction worker through socket
    private void read(){

    }

    // Send write request to transaction worker through socket
    private void write(){

    }

    private void sendMessage(OpMessage opMessage){

    }
}