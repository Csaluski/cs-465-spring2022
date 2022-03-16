package Records;

// Message types can be read/write or open/close transaction.
public enum OpMessageType {
    READ, WRITE, OPEN_TRANSACTION, CLOSE_TRANSACTION,
}
