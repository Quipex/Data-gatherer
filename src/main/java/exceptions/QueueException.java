package exceptions;

import java.io.IOException;

public class QueueException extends RuntimeException {
    public QueueException(String message) {
        super(message);
    }

    public QueueException(Throwable cause) {
        super(cause);
    }

    public QueueException(String s, IOException e) {
        super(s, e);
    }
}
