package Transaction;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TransactionManager {
    static List<Transaction> transactions = new ArrayList<Transaction>();

    int transactionID = 0;

    public TransactionManager(){

    }

    private int assignTransactionID(){
        return transactionID++;
    }

    // Create a TransactionWorker
    // Send transaction ID to proxy via stream
    private void openTransaction(Socket socket){
        
    }

    private void closeTransaction(){
        
    }

    // This should not be interfered until commit or abort
    public boolean validate(){
        return false;
    }

}