package at.ac.tuwien.sepr.groupphase.backend.exception;

import java.util.List;

public class AuthenticationException extends ErrorListException {

    /**
     * Creates an Object of type {@link ErrorListException}.
     *
     * @param messageSummary a summary of the problem
     * @param errors         a list of detail problem descriptions
     */
    public AuthenticationException(String messageSummary, List<String> errors) {
        super(messageSummary, errors);
    }

}
