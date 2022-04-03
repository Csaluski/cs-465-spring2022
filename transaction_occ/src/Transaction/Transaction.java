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

    public Transaction() {
    }

    void assignNumber(int number) {
        this.number = number;
    }

    void assignId(int id) {
        this.id = id;
    }

    public int readAccount(int accountId) {
        Integer balance;
        Account account;

        balance = writeSet.get(accountId);

        if (balance == null) {
            account = AccountManager.accounts.get(accountId);
            balance = account.getBalance();
        }

        if (!readSet.contains(accountId)) {
            readSet.add(accountId);
        }
        return balance;
    }

    public int writeAccount(int accountId, int amount) {
        writeSet.put(accountId, amount);
        return amount;
    }
}
