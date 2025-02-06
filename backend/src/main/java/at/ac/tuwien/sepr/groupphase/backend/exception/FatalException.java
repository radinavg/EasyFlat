package at.ac.tuwien.sepr.groupphase.backend.exception;

/**
 * Exception used to signal unexpected and unrecoverable errors.
 */
public class FatalException extends RuntimeException {

    /**
     * Creates a new object of type {@link FatalException}.
     *
     * @param message a description of the exception
     */
    public FatalException(String message) {
        super(message);
    }


    /**
     * Creates a new object of type {@link FatalException}.
     *
     * @param cause the cause
     */
    public FatalException(Throwable cause) {
        super(cause);
    }


    /**
     * Creates a new object of type {@link FatalException}.
     *
     * @param message a description of the exception
     * @param cause   the cause
     */
    public FatalException(String message, Throwable cause) {
        super(message, cause);
    }
}
