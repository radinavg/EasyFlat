package at.ac.tuwien.sepr.groupphase.backend.exception;

import java.util.List;

public class ConflictException extends ErrorListException {
    public ConflictException(String message) {
        super(message, List.of(message));
    }

    public ConflictException(String generalMessage, List<String> errors) {
        super(generalMessage, errors);
    }



}
