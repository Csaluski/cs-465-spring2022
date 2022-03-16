package Server;

import Account.AccountManager;
import PropertyHandler.PropertyHandler;
import Transaction.TransactionManager;

import java.io.IOException;

// Server loop with managers for accounts and transactions.
// Wakes up from accept returning a socket.
// Calls openTransaction on the transaction manager.
public class Server {
    public static AccountManager accountManager;
    public static TransactionManager transactionManager;

    public static void main(String[] args) throws IOException {
        // Server setup stuff.
        PropertyHandler propReader = new PropertyHandler("server.properties");
        int numAccounts = Integer.parseInt(propReader.getProperty("accounts"));
        int initBalance = Integer.parseInt(propReader.getProperty("balance"));
        accountManager = new AccountManager(numAccounts, initBalance);
        transactionManager = new TransactionManager();

        runServer();
    }

    // Accepts new transactions.
    private Socket accept() {

    }

    // Server loop goes here.
    private void runServer() {

    }
}

