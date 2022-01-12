import java.io.Serializable;
import java.net.Inet4Address;

public record NodeInfo(Inet4Address address, Integer port,
                       String name) implements Serializable {

    public Inet4Address getAddress() {
        return address;
    }

    public Integer getPort() {
        return port;
    }

    public String getName() {
        return name;
    }
}
