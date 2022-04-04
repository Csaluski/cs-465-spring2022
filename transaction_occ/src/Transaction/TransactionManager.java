package Transaction;

import Account.Account;
import Account.AccountManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Spawns off a TransactionWorker thread.
public class TransactionManager {
    static List<Transaction> transactions = new ArrayList<Transaction>();
    static AccountManager accountManager;

    int transactionCounter = 1; // atomic counter used for assigning both #s and ids.

    // Transaction manager constructor
    public TransactionManager(AccountManager accountManager) {
        TransactionManager.accountManager = accountManager;
    }

    // Only one thread is allowed to be in this function at once.
    // Could it ever happen that a thread executes the increment before returning the value?
    // Maybe, but it definitely won't when it's synchronized.
    private synchronized int getNextTransactionCounter() {
        return transactionCounter++;
    }

    // Create a TransactionWorker
    // Send transaction ID to proxy via stream
    public int openTransaction(Transaction transaction) {
        // Transaction ID is assigned in here.
        transaction.assignNumber(getNextTransactionCounter());
        return transaction.number;
    }

    // May return with an abort or a commit.
    // Should be the only place where an abort can happen, per discussion in class.
    // Validates and commits the transaction atomically.
    public synchronized boolean closeTransaction(Transaction transaction) {
        transaction.assignId(getNextTransactionCounter());
        boolean success = false;
        if (!checkConflict(transaction)) {
            System.out.println("Transaction with ID " + transaction.id + " succeeded validation, committing");
            success = commit(transaction);
            System.out.println("Total balance of all accounts is: $" + accountManager.checkTotal());
        }
        else {
            System.out.println("Transaction with ID " + transaction.id + " failed validation, aborting");
        }
        return success;
    }

    // checks for conflicts, if there are any it returns true, if there aren't then it
    // returns false.
    private boolean checkConflict(Transaction transaction) {
        // Compare transaction against overlapping transactions to verify that there were no conflicts
        // this uses a Stream since it is a filter->map->reduce pattern
        System.out.println("Validating transaction #" + transaction.number + " attempting to assign ID " + transaction.id);
        System.out.println("Checking other transaction IDs between " + transaction.number + " and " + transaction.id);
        return transactions.parallelStream()
                .filter(tran -> tran.id > transaction.number)
                .peek(tran -> System.out.println("Transaction with ID " + tran.id + " overlaps with validating #" + transaction.number + ", checking for conflict"))
                .anyMatch(tran -> checkSingleConflict(tran, transaction));
    }

    // Determine if there is conflict by checking read/write sets to determine if transaction has conflicts.
    private boolean checkSingleConflict(Transaction previous, Transaction validating) {
        boolean conflict = false;
        List<Integer> validatingReads = validating.readSet;
        HashMap<Integer, Integer> previousWrites = previous.writeSet;

        for (int acct : validatingReads){
            if (previousWrites.containsKey(acct)) {
                conflict = true;
                System.out.println("Transaction ID " + validating.id + " conflicts with previous #" + previous.id + ", closing transaction with ABORT");
                break;
            }
        }

        if (!conflict)
        {
            System.out.println("Transaction with ID " + validating.id + " does not conflict with transaction with ID " + previous.id);
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