package Transaction;

import Account.Account;
import Account.AccountManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Spanws off a TransactionWorker thread.
public class TransactionManager {
    static List<Transaction> transactions = new ArrayList<Transaction>();
    static AccountManager accountManager;

    int transactionCounter = 0; // atomic counter used for assigning both #s and ids.

    public TransactionManager() {
    }

    // Only one thread is allowed to be in this function at once.
    // Could it ever happen that a thread executes the increment before returning the value?
    // Maybe, but it definitely won't when it's synchronized.
    private synchronized int getNextTransactionCounter() {
        return transactionCounter++;
    }

    // Create a TransactionWorker
    // Send transaction ID to proxy via stream
    public void openTransaction(Transaction transaction) {
        // Transaction ID is assigned in here.
        transaction.assignNumber(getNextTransactionCounter());
    }

    // May return with an abort or a commit.
    // Should be the only place where an abort can happen, per discussion in class.
    // Validates and commits the transaction atomically.
    public synchronized boolean closeTransaction(Transaction transaction) {
        transaction.assignId(getNextTransactionCounter());
        boolean success = false;
        if (!checkConflict(transaction)) {
            success = commit(transaction);
        }
        return success;
    }

    // checks for conflicts, if there are any it returns true, if there aren't then it
    // returns false.
    private boolean checkConflict(Transaction transaction) {
        // Compare transaction against overlapping transactions to verify that there were no conflicts
        // this uses a Stream since it is a filter->map->reduce pattern
        return transactions.parallelStream()
                .filter(tran -> tran.id > transaction.number)
                .anyMatch(tran -> checkConflict(tran, transaction));
    }

    // Determine if there is conflict by checking read/write sets to determine if transaction has conflicts.
    private boolean checkConflict(Transaction previous, Transaction validating) {
        boolean conflict = false;
        List<Integer> validatingReads = validating.readSet;
        HashMap<Integer, Integer> previouseWrites = previous.writeSet;

        for (int acct : validatingReads){
            if (previouseWrites.containsKey(acct)) {
                conflict = true;
                break;
            }
        }
        return conflict;
    }

    // Synchronized so that only one thread can be here at once.
    private boolean commit(Transaction transaction) {
        // If transaction validated, commit by writing the write-set and adding the transaction
        // to the list of transactions
        for (Map.Entry<Integer, Integer> acct : transaction.writeSet.entrySet()) {
            Account updatedAccount = new Account(acct.getKey(), acct.getValue());
            AccountManager.accounts.put(acct.getKey(), updatedAccount);
        }
        transactions.add(transaction);
        return true;
    }

}