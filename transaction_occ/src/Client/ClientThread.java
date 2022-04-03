package Client;

import ProxyServer.Proxy;
import java.util.Random;

import PropertyHandler.PropertyHandler;
import Records.Account;

public class ClientThread extends Thread {
    private int fromAccountID = -1;
    private int toAccountID = -1;
    private int amount;
    Random rand;

    ClientThread(int seed) {
        rand = new Random(seed);
        try {
            PropertyHandler propReader = new PropertyHandler("./config/Server.properties");
            int numAccounts = Integer.parseInt(propReader.getProperty("ACCOUNTS"));
            while(fromAccountID == toAccountID){
                fromAccountID = rand.nextInt(numAccounts);
                toAccountID = rand.nextInt(numAccounts);
            }
            amount = rand.nextInt(10) + 1;
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Setup for necessary proxy.
    private void createProxy() {
        
    }

    public void run() {
        // Have proxy open a transaction
        // send read request to proxy
        // send write request to proxy
        // Have proxy close transaction
        int sleepDuration = rand.nextInt(5000);
        int transactionID;
        boolean success = false;
        Account accountFrom;
        Account accountTo;
        while (!success) {
            Proxy proxy = new Proxy();
            transactionID = proxy.openTransaction();
            System.out.println("Transaction #" + transactionID + " started, transfer $" + amount + ": " + fromAccountID + "->" + toAccountID);
            accountFrom = proxy.read(fromAccountID);
            accountFrom = proxy.write(fromAccountID, accountFrom.balance() - amount);
            try {
                Thread.sleep(sleepDuration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            accountTo = proxy.read(toAccountID);
            accountTo = proxy.write(toAccountID, accountTo.balance() + amount);
            success = proxy.closeTransaction();
        }
    }
}