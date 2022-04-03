package Records;

import java.io.Serializable;

// Record for Response Messages.
public record ResponseMessage(ResponseMessageType type, Object contents) implements Serializable {
}
