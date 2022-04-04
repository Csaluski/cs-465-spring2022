package Account;

import java.util.HashMap;
import java.util.Map;

// Manages a group of account objects via a HashMap.
// (Maybe LinkedList or ArrayList later if we change it.)
// High level operations on individual accounts.
public class AccountManager {
    public final static Map<Integer, Account> accounts = new HashMap<Integer, Account>();

    public AccountManager(int numAccounts, int initBalance) {
        for(int accountID = 0; accountID < numAccounts; accountID++) {
            accounts.put(accountID, new Account(accountID, initBalance));
        }
    }

    public int checkTotal() {
        return accounts.values().stream()
                .mapToInt(acct -> acct.getBalance())
                .sum();
    }
}