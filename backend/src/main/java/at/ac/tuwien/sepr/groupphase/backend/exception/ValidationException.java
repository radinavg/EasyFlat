package at.ac.tuwien.sepr.groupphase.backend.exception;

import java.util.List;

/**
 * Exception that signals, that data,
 * that came from outside the backend, is invalid.
 * The data violates some invariant constraint
 * (rather than one, that is imposed by the current data in the system).
 * Contains a list of all validations that failed when validating the piece of data in question.
 */
public class ValidationException extends ErrorListException {

    /**
     * Creates a new Object of Type {@link ValidationException}.
     *
     * @param messageSummary a short description of the error
     * @param errors         a List of all error messages
     */
    public ValidationException(String messageSummary, List<String> errors) {
        super(messageSummary, errors);
    }
}
