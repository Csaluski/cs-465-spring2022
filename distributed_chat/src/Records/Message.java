package Records;

import java.io.Serializable;

// Record for Messages, contents will be either of type String or NodeInfo, depending on type of MessageType.
// Makes us wish Java had union types
public record Message(MessageType type, Object contents) implements Serializable {
}
