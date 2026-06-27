package social.network.app.exception;

public class PostCreationException extends RuntimeException {
    public PostCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
