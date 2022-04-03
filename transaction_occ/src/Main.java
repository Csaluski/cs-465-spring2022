import Client.Client;
import Server.Server;

import java.io.IOException;
import java.util.Objects;

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

