package appserver.server;

import appserver.comm.Message;
import static appserver.comm.MessageTypes.JOB_REQUEST;
import static appserver.comm.MessageTypes.REGISTER_SATELLITE;
import appserver.comm.ConnectivityInfo;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;
import utils.PropertyHandler;

/**
 *
 * @author Dr.-Ing. Wolf-Dieter Otte
 */
public class Server {

    // Singleton objects - there is only one of them. For simplicity, this is not enforced though ...
    static SatelliteManager satelliteManager = null;
    static LoadManager loadManager = null;
    static ServerSocket serverSocket = null;

    public Server(String serverPropertiesFile) {

        // create satellite manager and load manager
        // ...
        satelliteManager = new SatelliteManager();
        loadManager = new LoadManager();
        // read server properties and create server socket
        // ...
        int port = -1;
        Properties properties;
        try {
            // read code server properties
            properties = new PropertyHandler(serverPropertiesFile);
            // read and output port
            port = Integer.parseInt(properties.getProperty("PORT"));
            System.out.println("[ApplicationServer] Port: " + port);        
        } catch (IOException ex) {
            System.err.println(ex);
        }
        
        try {
            serverSocket = new ServerSocket(port);
        } catch (Exception ex) {
            System.err.println("Error starting satellite on port " + port);
            System.exit(1);
        }
    }

    public void run() {
    // serve clients in server loop ...
    // when a request comes in, a ServerThread object is spawned
    // ...
        while(true){
            try {
                // get socket from listener
                Socket socket = serverSocket.accept();

                // start new thread for job request
                (new ServerThread(socket)).start();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    // objects of this helper class communicate with satellites or clients
    private class ServerThread extends Thread {

        Socket client = null;
        ObjectInputStream readFromNet = null;
        ObjectOutputStream writeToNet = null;
        Message message = null;

        private ServerThread(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            // set up object streams and read message
            // ...
            try {
                readFromNet = new ObjectInputStream(client.getInputStream());
                message = (Message) readFromNet.readObject();
                writeToNet = new ObjectOutputStream(client.getOutputStream());
            } catch(IOException | ClassNotFoundException e){
                System.err.println(e);
            }
            
            // process message
            switch (message.getType()) {
                case REGISTER_SATELLITE:
                    // read satellite info
                    // ...
                    ConnectivityInfo satelliteInfo = (ConnectivityInfo) message.getContent();
                    
                    // register satellite
                    synchronized (Server.satelliteManager) {
                        // ...
                        Server.satelliteManager.registerSatellite(satelliteInfo);
                    }

                    // add satellite to loadManager
                    synchronized (Server.loadManager) {
                        // ...
                        Server.loadManager.satelliteAdded(satelliteInfo.getName());
                    }
                    System.out.println("[ServerThread.run] Register satellite " + satelliteInfo.getName());

                    break;

                case JOB_REQUEST:
                    System.err.println("\n[ServerThread.run] Received job request");

                    String satelliteName = null;
                    ConnectivityInfo targetSatellite = null;
                    synchronized (Server.loadManager) {
                        // get next satellite from load manager
                        // ...
                        try {
                            satelliteName = Server.loadManager.nextSatellite();
                            
                        } catch(Exception e){
                            System.err.println(e);
                        }
                        
                        // get connectivity info for next satellite from satellite manager
                        // ...
                        targetSatellite = Server.satelliteManager.getSatelliteForName(satelliteName);
                        System.out.println("[ServerThread.run] Connect to satellite " + targetSatellite.getName());
                    }
                    Socket satellite = null;
                    // connect to satellite
                    // ...
                    try {
                        satellite = new Socket(targetSatellite.getHost(), targetSatellite.getPort());
                    } catch(IOException e){
                        System.err.println(e);
                    }

                    // open object streams,
                    // forward message (as is) to satellite,
                    // receive result from satellite and
                    // write result back to client
                    // ...

                    try {
                        // object streams
                        ObjectOutputStream toSatellite = new ObjectOutputStream(satellite.getOutputStream());
                        ObjectInputStream fromSatellite = new ObjectInputStream(satellite.getInputStream());
                        
                        
                        toSatellite.writeObject(message);
                        
                        Long result = (Long) fromSatellite.readObject();
                        
                        
                        writeToNet.writeObject(result);
                        
                        toSatellite.close();
                        fromSatellite.close();
                        readFromNet.close();
                        writeToNet.close();
                        // create job request message
                        
                    } catch(IOException | ClassNotFoundException e) {
                        System.err.println(e);
                        System.err.println("Cannot create object streams.");
                        System.exit(1);
                    }
                    break;

                default:
                    System.err.println("[ServerThread.run] Warning: Message type not implemented");
            }
        }
    }

    // main()
    public static void main(String[] args) {
        // start the application server
        Server server = null;
        if(args.length == 1) {
            server = new Server(args[0]);
        } else {
            server = new Server("../../config/Server.properties");
        }
        server.run();
    }
}
