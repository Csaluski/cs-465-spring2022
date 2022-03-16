package Transaction;

import java.net.Socket;

public class TransactionWorker extends Thread {
    private final Transaction transaction;
    private final Socket socket;

    TransactionWorker(Socket socket, int number){
        this.socket = socket;

        this.transaction = new Transaction(number);

    }

    private void receiveMessage() {

    }

    private int readBalance(int accountID){
        return 0;
    }

    private int writeBalance(int accountID){
        return 0;
    }
    
    // Receive and process messages from client
    @Override
    public void run(){
        // Enters a loop that
        // receives/processes messages (big switch statement)
        // determines transaction needs to be open
        //      creates transaction object
        //      assigns an id to transaction
        //      stores reference in list of transactions held by manager
        // sends transaction ID back to proxy
        // runs validate, if conflict then abort and roll back effect
        // if no abort, write transaction
    }
}