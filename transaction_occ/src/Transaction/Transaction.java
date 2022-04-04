package Transaction;

import Account.AccountManager;
import Account.Account;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Transaction {
    public int id;
    public int number;
    // list of the accounts that have been read from
    public final List<Integer> readSet = new ArrayList<>();
    // set mapping account numbers to balances
    public final HashMap<Integer, Integer> writeSet = new HashMap<>();

    // Transaction constructor
    public Transaction() {
    }

    // Assigns transaction number
    void assignNumber(int number) {
        this.number = number;
    }

    // Assigns transaction ID
    void assignId(int id) {
        this.id = id;
    }

    // Checks and returns account balance
    public int readAccount(int accountId) {
        Integer balance;
        Account account;

        // Get balance from the record of what transactions an account has done
        balance = writeSet.get(accountId);

        // If that info is not available, get it from the account manager
        if (balance == null) {
            account = AccountManager.accounts.get(accountId);
            balance = account.getBalance();
        }

        // Record account ID
        if (!readSet.contains(accountId)) {
            readSet.add(accountId);
        }

        return balance;
    }

    // Writes to account and returns amount written
    public int writeAccount(int accountId, int amount) {
        writeSet.put(accountId, amount);
        return amount;
    }
}
