package Client;

public class Client {

    // Handles basic client setup tasks and loops/stuff for ClientThreads.
    public static void main(String[] args) {
        int numClients = Integer.parseInt(args[0]);

        Client client = new Client();
        for (int clientId = 0; clientId <= numClients; clientId++){
            new Thread(new ClientThread(clientId)).start();
        }
        // Creates proxy.
        // Calls openTransaction() on the proxy.
    }

}
