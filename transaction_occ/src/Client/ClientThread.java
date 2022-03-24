package Client;


import ProxyServer.Proxy;

public class ClientThread {
    private final Proxy proxy = null;
    private int transactionID;

    ClientThread() {
        
    }

    // Setup for necessary proxy.
    private void createProxy() {
        proxy = new Proxy();
    }

    private void run() {
        // Have proxy open a transaction
        // send read request to proxy
        // send write request to proxy
        // Have proxy close transaction
        proxy.openTransaction();
        proxy.read(1);
        proxy.write(1);
        proxy.closeTransaction();
    }
}