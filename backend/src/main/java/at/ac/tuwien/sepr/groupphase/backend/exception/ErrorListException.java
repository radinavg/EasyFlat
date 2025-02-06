package at.ac.tuwien.sepr.groupphase.backend.exception;

import java.util.Collections;
import java.util.List;

/**
 * Common super class for exceptions that report a list of errors
 * back to the user, when the given data did not pass a certain kind of checks.
 */
public abstract class ErrorListException extends Exception {

    /**
     * a List of Error Messages.
     */
    private final List<String> errors;

    /**
     * A summary of the messages.
     */
    private final String messageSummary;

    /**
     * Creates an Object of typ {@link ErrorListException}.
     *
     * @param messageSummary a summary of the problem
     * @param errors         a list of detail problem descriptions
     */
    public ErrorListException(String messageSummary, List<String> errors) {
        super(messageSummary);
        this.messageSummary = messageSummary;
        this.errors = errors;
    }

    /**
     * See {@link Throwable#getMessage()} for general information about this method.
     *
     * <p>Note: this implementation produces the message
     * from the {@link #summary} and the list of {@link #errors}
     */
    @Override
    public String getMessage() {
        return "%s : [%s.]"
            .formatted(messageSummary, String.join(", ", errors));
    }

    /**
     * Gives a description of this exception.
     *
     * @return a String
     */
    public String summary() {
        return messageSummary;
    }

    /**
     * Gives a List of errors.
     *
     * @return an unmodifiable List of error messages
     */
    public List<String> errors() {
        return Collections.unmodifiableList(errors);
    }
}
