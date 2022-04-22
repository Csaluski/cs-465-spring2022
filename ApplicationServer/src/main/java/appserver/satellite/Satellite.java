package appserver.satellite;

import appserver.job.Job;
import appserver.comm.ConnectivityInfo;
import appserver.job.UnknownToolException;
import appserver.comm.Message;
import static appserver.comm.MessageTypes.JOB_REQUEST;
import static appserver.comm.MessageTypes.REGISTER_SATELLITE;
import appserver.job.Tool;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.PropertyHandler;

/**
 * Class [Satellite] Instances of this class represent computing nodes that execute jobs by
 * calling the callback method of tool a implementation, loading the tool's code dynamically over a network
 * or locally from the cache, if a tool got executed before.
 *
 * @author Dr.-Ing. Wolf-Dieter Otte
 */
public class Satellite extends Thread {

    private ConnectivityInfo satelliteInfo = new ConnectivityInfo();
    private ConnectivityInfo serverInfo = new ConnectivityInfo();
    private HTTPClassLoader classLoader = null;
    private Hashtable<String, Tool> toolsCache = null;

    public Satellite(String satellitePropertiesFile, String classLoaderPropertiesFile, String serverPropertiesFile) {
        Properties properties;
        // read this satellite's properties and populate satelliteInfo object,
        // which later on will be sent to the server
        // ...
        try {
            // read properties
            // read and output name
            properties = new PropertyHandler(satellitePropertiesFile);
            String name = properties.getProperty("NAME");
            System.out.println("[satellite.Sattellite] NAME: " + name);
            // read and output port
            int port = Integer.parseInt(properties.getProperty("PORT"));
            System.out.println("[satellite.Sattellite] Port: " + port);

            // populate satelliteInfo object to send to server later
            satelliteInfo.setName(name);
            satelliteInfo.setPort(port);
        } catch (IOException ex) {
            System.err.println(ex);
        }
        
        // read properties of the application server and populate serverInfo object
        // other than satellites, the as doesn't have a human-readable name, so leave it out
        // ...
        try {
            // read properties
            // read and output host
            properties = new PropertyHandler(serverPropertiesFile);
            String appServerHost = properties.getProperty("HOST");
            // read and output port
            System.out.println("[ApplicationServer] Host: " + appServerHost);
            int appServerPort = Integer.parseInt(properties.getProperty("PORT"));
            System.out.println("[ApplicationServer] Port: " + appServerPort);

            // set serverInfo values
            serverInfo.setHost(appServerHost);
            serverInfo.setPort(appServerPort);
        } catch (IOException ex) {
            System.err.println(ex);
        }
        
        // read properties of the code server and create class loader
        // -------------------
        // ...
        try {
            // read code server properties
            // read and output host
            properties = new PropertyHandler(classLoaderPropertiesFile);
            String webServerHost = properties.getProperty("HOST");
            System.out.println("[WebServer] Host: " + webServerHost);
            //read and output port
            int webServerPort = Integer.parseInt(properties.getProperty("PORT"));
            System.out.println("[WebServer] Port: " + webServerPort);

            // create class loader
            classLoader = new HTTPClassLoader(webServerHost, webServerPort);
        } catch (IOException ex) {
            System.err.println(ex);
        }
        
        // create tools cache
        // -------------------
        // ...
        toolsCache = new Hashtable<>();
    }

    @Override
    public void run() {

        // register this satellite with the SatelliteManager on the server
        // ---------------------------------------------------------------
        // ...
        // cannot do this yet as this part is not implemented
        
        // create server socket
        // ---------------------------------------------------------------
        // ...
        ServerSocket listenSocket = null;
        try {
            listenSocket = new ServerSocket(satelliteInfo.getPort());
        } catch (Exception ex) {
            System.err.println("Error starting satellite on port " + satelliteInfo.getPort());
            System.exit(1);
        }
        
        // start taking job requests in a server loop
        // ---------------------------------------------------------------
        // ...
        while(true){
            try {
                // get socket from listener
                Socket socket = listenSocket.accept();

                // start new thread for job request
                (new SatelliteThread(socket, this)).start();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    // inner helper class that is instanciated in above server loop and processes single job requests
    private class SatelliteThread extends Thread {

        Satellite satellite = null;
        Socket jobRequest = null;
        ObjectInputStream readFromNet = null;
        ObjectOutputStream writeToNet = null;
        Message message = null;

        // constructor
        SatelliteThread(Socket jobRequest, Satellite satellite) {
            this.jobRequest = jobRequest;
            this.satellite = satellite;
        }

        @Override
        public void run() {
            // setting up object streams
            // ...
            ObjectInputStream in = null;
            ObjectOutputStream out = null;
            try {
                // object streams
                in = new ObjectInputStream(jobRequest.getInputStream());
                out = new ObjectOutputStream(jobRequest.getOutputStream());

                // create job request message
                message = (Message) in.readObject();
            } catch(IOException | ClassNotFoundException e) {
                System.err.println(e);
                System.err.println("Cannot create object streams.");
                System.exit(1);
            }
            
            
            // reading message
            // ...
            switch (message.getType()) {
                case JOB_REQUEST:
                    // processing job request
                    // ...

                    // set up job request
                    Job job = (Job) message.getContent();
                    String toolName = job.getToolName();
                    Long num = (Long) job.getParameters();

                    // try to run job (get name, calculate result, send result)
                    try {
                        Tool toolObject = satellite.getToolObject(toolName);
                        Long result = (Long) toolObject.go(num);
                        out.writeObject(result);
                    } catch(UnknownToolException e) {
                        System.err.println(e);
                        System.err.println("Cannot find " + toolName + "object");
                    } catch(Exception e) {
                        System.err.println(e);
                    }                
                    break;

                default:
                    System.err.println("[SatelliteThread.run] Warning: Message type not implemented");
            }
        }
    }

    /**
     * Aux method to get a tool object, given the fully qualified class string
     * If the tool has been used before, it is returned immediately out of the cache,
     * otherwise it is loaded dynamically
     */
    public Tool getToolObject(String toolClassString) throws UnknownToolException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException {

        Tool toolObject = null;

        // if good to go because tool has been used before, return it right away
        if((toolObject = toolsCache.get(toolClassString)) != null) {
            return toolObject;
        }

        // load code dynamically if needed
        Class<?> toolClass = classLoader.findClass(toolClassString);
        try {
            toolObject = (Tool) toolClass.getDeclaredConstructor().newInstance();
            toolsCache.put(toolClassString, toolObject);
        } catch (InvocationTargetException e) {
            System.err.println(e);
        }
        
        return toolObject;
    }

    public static void main(String[] args) {
        // start the satellite
        Satellite satellite = new Satellite(args[0], args[1], args[2]);
        satellite.run();
    }
}
