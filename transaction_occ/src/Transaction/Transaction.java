package Transaction;

import Account.AccountManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Transaction{
    int id;
    int number;
    List<Integer> readSet;
    HashMap<Integer, Integer> writeSet;


    public Transaction(int number) {
        this.number = number;
        List<Integer> readSet = new ArrayList<>();
        HashMap<Integer, Integer> writeSet = new HashMap<>();
    }

    void assignId(int id){
        this.id = id;
    }

    int readAccount(int accountId)
    {
        AccountManager.accounts.get(accountId);
        return 0;
    }

    void writeAccount(int accountId, int amount){

    }
}
