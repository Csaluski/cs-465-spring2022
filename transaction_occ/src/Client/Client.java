package Client;

public class Client {

    // Handles basic client setup tasks and loops/stuff for ClientThreads.
    public static void main(String[] args) {
        // Creates proxy.
        // Calls openTransaction() on the proxy.
    }

    private void run(){
        while(true){
            new Thread(new ClientThread(13)).start();
        }
    }
}
