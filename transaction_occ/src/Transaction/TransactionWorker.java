package Transaction;

import Records.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

// Instances of this class are created when the TransactionManager accepts a new connection from a Proxy,
// then this class takes the role of handling all communications with the proxy until the client
// closes the transaction
public class TransactionWorker extends Thread {
    private final TransactionManager transactionManager;
    private final Transaction transaction;
    private final Socket socket;
    private ObjectInputStream fromClient;
    private ObjectOutputStream toClient;
    public TransactionWorker(Socket socket, TransactionManager manager) {
        this.socket = socket;
        this.transactionManager = manager;
        this.transaction = new Transaction();
    }

    private int readBalance(int accountID) {
        return transaction.readAccount(accountID);
    }

    private int writeBalance(int accountID, int amount) {
        return transaction.writeAccount(accountID, amount);
    }

    private ResponseMessage processOperation(OpMessage operation) {
        ResponseMessage response;
        switch (operation.type()) {
            case READ -> {
                int accountId = (int) operation.contents();
                int balance = readBalance(accountId);
                Account account = new Account(accountId, balance);
                response = new ResponseMessage(ResponseMessageType.READ, account);
            }
            case WRITE -> {
                Account writeAccount = (Account) operation.contents();
                int newBalance = writeBalance(writeAccount.id(), writeAccount.balance());
                Account returnAccount = new Account(writeAccount.id(), newBalance);
                response = new ResponseMessage(ResponseMessageType.WRITE, returnAccount);
            }

            case OPEN_TRANSACTION -> {
                transactionManager.openTransaction(transaction);
                response = new ResponseMessage(ResponseMessageType.OPEN, null);
            }
            case CLOSE_TRANSACTION -> {
                boolean validated = transactionManager.closeTransaction(transaction);
                ResponseMessageType responseMessageType = validated ? ResponseMessageType.SUCCESS : ResponseMessageType.ABORT;
                response = new ResponseMessage(responseMessageType, transaction.number);
            }
            default -> {
                response = new ResponseMessage(ResponseMessageType.ABORT, null);
            }
        }
        return response;
    }


    // Receive and process messages from client
    @Override
    public void run() {
        boolean continueTransaction = false;
        try {
            toClient = new ObjectOutputStream(socket.getOutputStream());
            fromClient = new ObjectInputStream(socket.getInputStream());
            continueTransaction = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        OpMessage messageFromClient = null;
        ResponseMessage responseMessage = null;
        while (continueTransaction) {
            try {
                messageFromClient = (OpMessage) fromClient.readObject();
                responseMessage = processOperation(messageFromClient);
                if (messageFromClient.type() == OpMessageType.CLOSE_TRANSACTION){
                    continueTransaction = false;
                }
                toClient.writeObject(responseMessage);
            } catch (Exception e) {
                e.printStackTrace();
                continueTransaction = false;
            }
        }

        // Enters a loop that
        // receives/processes messages (big switch statement)
        // determines transaction needs to be open
        //      creates transaction object
        //      assigns an id to transaction
        //      stores reference in list of transactions held by manager
        // sends transaction ID back to proxy
        // runs validate, if conflict then abort and roll back effect
        // if no abort, write transaction
    }
}