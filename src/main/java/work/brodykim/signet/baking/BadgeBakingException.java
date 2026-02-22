package work.brodykim.signet.baking;

/**
 * Exception thrown when a badge baking or extraction operation fails.
 */
public class BadgeBakingException extends Exception {

    public BadgeBakingException(String message) {
        super(message);
    }

    public BadgeBakingException(String message, Throwable cause) {
        super(message, cause);
    }
}
