package App;

public class Proxy {
    // Send messages to transaction server through this socket
    private final Socket clientSocket;

    Proxy(){

    }

    private Message makeMessage(){

    }

    // return transaction ID
    // connect to App.Server by using server port and ip
    // get transaction ID from transaction manager via stream
    private int openTransaction(){

    }

    private void closeTransaction(){
        
    }

    // Send read request to transaction worker through socket
    private void read(){

    }

    // Send write request to transaction worker through socket
    private void write(){

    }

    private void sendMessage(Message message){

    }
}