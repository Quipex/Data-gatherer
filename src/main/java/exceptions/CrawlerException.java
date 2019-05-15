package exceptions;

public class CrawlerException extends RuntimeException {
    public CrawlerException(Throwable cause) {
        super(cause);
    }

    public CrawlerException(String message) {
        super(message);
    }
}
