package Records;

import java.io.Serializable;

// immutable read only account record used for communication over network
public record Account(int id, int balance) implements Serializable {
}
