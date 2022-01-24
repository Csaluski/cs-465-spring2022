import java.io.Serializable;

public class Message implements Serializable {

    private MessageType type;
    private Object contents;

    public Message(MessageType type, Object contents) {
        this.type = type;
        this.contents = contents;
    }

    public MessageType type() {
        return type;
    }

    public Object contents() {
        return contents;
    }
}
