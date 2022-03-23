package Transaction;

import Account.AccountManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Transaction{
    int id;
    int number;
    // list of the accounts that have been read from
    List<Integer> readSet = new ArrayList<>();
    // set mapping account numbers to balances
    HashMap<Integer, Integer> writeSet = new HashMap<>();

    public Transaction() {
    }

    void assignNumber(int number){
        this.number = number;
    }

    public int readAccount(int accountId)
    {
        Integer balance;

        balance = writeSet.get(accountId);

        if (balance == null)
        {
            AccountManager.accounts.get(accountId);
        }

        if (!readSet.contains(accountId))
        {
            readSet.add(accountId);
        }
        return balance;
    }

    public int writeAccount(int accountId, int amount){
        writeSet.put(accountId, amount);
        return amount;
    }
}
