import Client.Client;
import Server.Server;
import java.io.IOException;
import java.util.Objects;

// Start server or client based on args
// This is for convenience of the assignment, so everything can be packaged into one space and run
// from the same spot without extra work
public class Main {
    public static void main(String[] args) throws IOException {
        if ((args.length > 0) && (Objects.equals(args[0], "server"))) {
            Server.main(args);
        }
        else {
            Client.main(args);
        }
    }
}

