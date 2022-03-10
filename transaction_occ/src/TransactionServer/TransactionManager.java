package App;

public class TransactionManager {
    static List<Integer> readSet = new ArrayList<Integer>();
    static Map<Integer, Integer> writeSet = new HashMap<Integer, Integer>();

    int transactionID = 0;

    TransactionManager(){

    }

    private int assignTransactionID(){
        return transactionID++;
    }

    // Craete a TransactionWorker
    // Send transaction ID to proxy via stream
    private void openTransaction(Socket socket){
        
    }

    private void closeTransaction(){
        
    }

    // This should not be interfered until commit or abort
    public void validate(){

    }

}