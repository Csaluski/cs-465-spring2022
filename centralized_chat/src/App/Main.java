package App;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Please run with arguments of either 'server' or 'client nickname'");
        }

        if (args[0].equals("server")) {
            ServerRun.main(args);
        }

        else if (args[0].equals("client")) {
            Client.main(args);
        }
    }
}