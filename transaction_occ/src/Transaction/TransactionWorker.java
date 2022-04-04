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

    // Read account balance via transaction record
    private int readBalance(int accountID) {
        return transaction.readAccount(accountID);
    }

    // Write account balance to transaction record
    private int writeBalance(int accountID, int amount) {
        return transaction.writeAccount(accountID, amount);
    }

    // Handles logic for what kind of response to send out
    private ResponseMessage processOperation(OpMessage operation) {
        ResponseMessage response;
        switch (operation.type()) {
            case READ -> { //if reading, check and return balance
                int accountId = (int) operation.contents();
                int balance = readBalance(accountId);
                Account account = new Account(accountId, balance);
                response = new ResponseMessage(ResponseMessageType.READ, account);
            }
            case WRITE -> { // if writing, write and return result
                Account writeAccount = (Account) operation.contents();
                int newBalance = writeBalance(writeAccount.id(), writeAccount.balance());
                Account returnAccount = new Account(writeAccount.id(), newBalance);
                response = new ResponseMessage(ResponseMessageType.WRITE, returnAccount);
            }
            case OPEN_TRANSACTION -> { // open a new transaction and send back transaction number
                int number = transactionManager.openTransaction(transaction);
                response = new ResponseMessage(ResponseMessageType.OPEN, new Integer(number));
            }
            case CLOSE_TRANSACTION -> { // validate and then close a transaction, send success/abort depending on result
                boolean validated = transactionManager.closeTransaction(transaction);
                ResponseMessageType responseMessageType = validated ? ResponseMessageType.SUCCESS : ResponseMessageType.ABORT;
                response = new ResponseMessage(responseMessageType, transaction.number);
            }
            default -> { // default is an abort
                response = new ResponseMessage(ResponseMessageType.ABORT, null);
            }
        }
        return response;
    }

    // Receive and process messages from client
    // Enters a loop that
    // receives/processes messages (big switch statement in other function)
    // determines transaction needs to be open
    //      creates transaction object
    //      assigns an id to transaction
    //      stores reference in list of transactions held by manager
    // sends transaction ID back to proxy
    // runs validate, if conflict then abort and roll back effect
    // if no abort, write transaction
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
                System.out.println("Transaction #" + transaction.number + " conducting " + messageFromClient + ", response is " + responseMessage);
                if (messageFromClient.type() == OpMessageType.CLOSE_TRANSACTION){
                    continueTransaction = false;
                }
                toClient.writeObject(responseMessage);
            } catch (Exception e) {
                e.printStackTrace();
                continueTransaction = false;
            }
        }
    }
}