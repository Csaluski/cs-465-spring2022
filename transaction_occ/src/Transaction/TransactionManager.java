package Transaction;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

// Spanws off a TransactionWorker thread.
public class TransactionManager {
    static List<Transaction> transactions = new ArrayList<Transaction>();

    int transactionID = 0;
    int lastTransactionNumber = 0; // Tracks last assigned TN.

    public TransactionManager() {

    }

    public int getNewTransactionId() {
        return transactionID++;
    }

    // Create a TransactionWorker
    // Send transaction ID to proxy via stream
    private void openTransaction(Socket socket) {
        // Transaction ID is assigned in here.
        // Remember last assigned transaction number.
    }

    // May return with an abort or a commit.
    // Should be the only place where an abort can happen,
    // per discussion in class.
    private void closeTransaction() {

    }

    // This should not be interfered until commit or abort
    public boolean validate(Transaction transaction) {
        // Transaction number is assigned in the beginning of this phase.

        // Determine via transaction numbers which are overlapping based
        // on read/write sets to determine if you need to abort here.
        return false;
    }

    // Handles synchronization checks.
    // Synchronized so that only one thread can be here at once.
    synchronized boolean commit(Transaction transaction) {
        // If transaction validated, commit + write data
        //      + tell client transaction is completed
        // If validation fails, abort and tell client
        return false;
    }

}