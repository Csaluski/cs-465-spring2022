package Records;

import java.io.Serializable;
import java.net.Inet4Address;

// Record for NodeInfo, name will be a client's logical name or "SERVER" if node is created as a server.
public record NodeInfo(Inet4Address address, Integer port,
                       String name) implements Serializable {}
