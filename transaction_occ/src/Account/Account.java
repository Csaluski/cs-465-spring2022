package Account;

import java.io.Serializable;

// Simple account object with functionality for adding/subtracting
// (writes), and reading balance.
public class Account implements Serializable {
    private int balance;
    private int id;

    public Account(int balance) {
        this.balance = balance;
    }

    public Account(int id, int balance) {
        this.balance = balance;
        this.id = id;
    }

    public void withdraw(int amount) {
        balance -= amount;
    }

    public void deposit(int amount) {
        balance += amount;
    }

    public int getBalance() {
        return balance;
    }
}