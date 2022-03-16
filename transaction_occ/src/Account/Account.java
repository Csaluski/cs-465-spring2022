package Account;

// Simple account object with functionality for adding/subtracting
// (writes), and reading balance.
public class Account {
    private int balance;
    private int id;

    public Account(int balance) {
        this.balance = balance;
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