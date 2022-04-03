package Account;

import java.util.HashMap;
import java.util.Map;

// Manages a group of account objects via a HashMap.
// (Maybe LinkedList or ArrayList later if we change it.)
// High level operations on individual accounts.
public class AccountManager {
    public final static Map<Integer, Account> accounts = new HashMap<Integer, Account>();

    public AccountManager(int numAccounts, int initBalance) {
        for(int i = 0; i < numAccounts; i++) {
            accounts.put(i, new Account(initBalance, i));
        }
    }

    public int read(int accountID) {
        return accounts.get(accountID).getBalance();
    }

    public void write(int accountID, int amount) {
        // Commit effects of validated transaction.
    }
}