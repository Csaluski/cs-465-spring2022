package Client;

import ProxyServer.Proxy;
import java.util.Random;

import PropertyHandler.PropertyHandler;

public class ClientThread extends Thread {
    private final Proxy proxy;
    private int transactionID;
    private int fromAccountID = -1;
    private int toAccountID = -1;
    private int amount;

    ClientThread(int seed) {
        proxy = new Proxy();
        Random rand = new Random(seed);
        try {
            PropertyHandler propReader = new PropertyHandler("server.properties");
            int numAccounts = Integer.parseInt(propReader.getProperty("accounts"));
            while(fromAccountID == toAccountID){
                fromAccountID = rand.nextInt(numAccounts);
                toAccountID = rand.nextInt(numAccounts);
            }
            amount = rand.nextInt(10000) + 1;
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
        proxy.openTransaction();
        proxy.read(fromAccountID);
        proxy.write(fromAccountID, amount);
        proxy.read(toAccountID);
        proxy.write(toAccountID, amount);
        proxy.closeTransaction();
    }
}