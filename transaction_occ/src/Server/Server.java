package Server;

import Account.AccountManager;
import PropertyHandler.PropertyHandler;
import Transaction.Transaction;
import Transaction.TransactionManager;
import Transaction.TransactionWorker;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// Server loop with managers for accounts and transactions.
// Wakes up from accept returning a socket.
// Calls openTransaction on the transaction manager.
public class Server {
    public static AccountManager accountManager;
    public static TransactionManager transactionManager;
    private static ServerSocket listenSocket;

    public static void main(String[] args) throws IOException {
        // Server setup stuff.
        PropertyHandler propReader = new PropertyHandler("src/config/Server.properties");
        int numAccounts = Integer.parseInt(propReader.getProperty("ACCOUNTS"));
        int initBalance = Integer.parseInt(propReader.getProperty("BALANCE"));
        int listenPort = Integer.parseInt(propReader.getProperty("SERVER_PORT"));
        accountManager = new AccountManager(numAccounts, initBalance);
        transactionManager = new TransactionManager();
        listenSocket = new ServerSocket(listenPort);

        Server server = new Server();
        server.run();
    }

    // Accepts new transactions when a proxy connects to the waiting socket,
    // when the socket is opened it creates a new TransactionWorker
    // that it passes the socket to
    // and handles it for the remainder of the session
    private void run() {
        while (true) {
            try {
                Socket socket = listenSocket.accept();
                TransactionWorker workerThread = new TransactionWorker(socket, transactionManager);
                workerThread.start();
            }
            catch (Exception e) {

            }
        }
    }
}

