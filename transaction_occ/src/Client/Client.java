package Client;

import PropertyHandler.PropertyHandler;
import java.io.IOException;

public class Client {

    // Handles basic client setup tasks and loops/stuff for ClientThreads.
    public static void main(String[] args) throws IOException {
        PropertyHandler propReader = new PropertyHandler("./config/Client.properties");
        int numClients = Integer.parseInt(propReader.getProperty("NUM_CLIENTS"));

        for (int clientId = 0; clientId < numClients; clientId++) {
            new Thread(new ClientThread(clientId)).start();
        }
    }

}
