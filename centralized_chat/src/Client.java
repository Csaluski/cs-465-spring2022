import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;

public class Client {

    private final NodeInfo nodeInfo;
    private final NodeInfo serverInfo;
    private final ServerSocket listenSocket;

    // TODO figure out how to get local IP in a real way
    private Client(String name) throws IOException {
        Inet4Address address = (Inet4Address) Inet4Address.getByName("localhost");
        int port;
        try ( ServerSocket socket = new ServerSocket(0)) {
            port = socket.getLocalPort();
            this.listenSocket = socket;
        }
        this.nodeInfo = new NodeInfo(address, port, name);
        this.serverInfo = new NodeInfo(address, 23467, "SERVER");
    }

    private Message createMessage(String rawMessage) {
        return switch (rawMessage) {
            case "JOIN"  -> new Message(MessageType.JOIN, nodeInfo);
            case "LEAVE" -> new Message(MessageType.LEAVE, nodeInfo);
            default      -> new Message(MessageType.NODE, rawMessage);
        };
    }

    public static void main(String[] args) {

    }

}
