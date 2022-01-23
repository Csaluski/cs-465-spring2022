package App;

public class Main {
    public static void main(String[] args) throws Exception {

        // The functionality of main here is to choose if you are running a server or a client and start the program properly.
        // This is a little more convenient because it allows us to package everything into one .jar file and
        // cut down on extra files for us and the grader.

        if (args.length == 0) {
            System.out.println("Please run with arguments of either 'server' or 'client nickname'.");
        }
        if (args[0].equals("server")) {
            ServerRun.main(args);
        }
        else if (args[0].equals("client")) {
            Client.main(args);
        }
    }
}