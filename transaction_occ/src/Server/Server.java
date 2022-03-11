package Server;

import Account.AccountManager;
import PropertyHandler.PropertyHandler;
import Transaction.TransactionManager;

import java.io.IOException;

public class Server {
    public static AccountManager accountManager;
    public static TransactionManager transactionManager;

    public static void main(String[] args) throws IOException {
        PropertyHandler propReader = new PropertyHandler("server.properties");
        int numAccounts = Integer.parseInt(propReader.getProperty("accounts"));
        int initBalance = Integer.parseInt(propReader.getProperty("balance"));
        accountManager = new AccountManager(numAccounts, initBalance);
        transactionManager = new TransactionManager();
    }
}
