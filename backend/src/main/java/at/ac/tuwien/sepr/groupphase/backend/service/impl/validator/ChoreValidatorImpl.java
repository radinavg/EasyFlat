package at.ac.tuwien.sepr.groupphase.backend.service.impl.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChoreDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Chore;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.interfaces.ChoreValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ChoreValidatorImpl implements ChoreValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Validator validator;

    public ChoreValidatorImpl(Validator validator) {
        this.validator = validator;
    }

    @Override
    public void validateForCreate(ChoreDto chore) throws ValidationException, ConflictException {
        LOGGER.trace("validateForCreate({})", chore);

        checkValidationForCreate(chore);
        checkConflictForCreate(chore);
    }

    private void checkConflictForCreate(ChoreDto chore) throws ConflictException {
        LOGGER.trace("checkConflictForCreate({})", chore);

        List<String> errors = new ArrayList<>();
        if (chore.id() != null) {
            errors.add("The id must be null");
        }

        if (!errors.isEmpty()) {
            throw new ConflictException("There is a conflict with persisted data", errors);
        }
    }

    private void checkValidationForCreate(ChoreDto chore) throws ValidationException {
        LOGGER.trace("checkValidationForCreate({})", chore);
        Set<ConstraintViolation<ChoreDto>> validationViolations = validator.validate(chore);

        List<String> violations = new ArrayList<>();
        if (chore.points() != null) {
            if (chore.points().isBlank() || isNotNumeric(chore.points())) {
                violations.add("The points must be a number");
            } else {
                if (!isValidInteger(chore.points())) {
                    violations.add("The points cannot have any decimal places");
                    if (Float.parseFloat(chore.points()) < 0) {
                        violations.add("The points must be a positive number");
                    }
                } else {
                    if (Integer.parseInt(chore.points()) < 0) {
                        violations.add("The points must be a positive number");
                    } else if (Integer.parseInt(chore.points()) > 100) {
                        violations.add("The points cannot be greater than 100");
                    }
                }
            }
        }

        List<String> errorMessages = Stream.concat(
                validationViolations.stream().map(ConstraintViolation::getMessage),
                violations.stream()
            )
            .collect(Collectors.toList());

        if (!errorMessages.isEmpty()) {
            throw new ValidationException("Not valid data", errorMessages);
        }
    }

    @Override
    public void validateForUpdate(Chore chore) throws ValidationException, ConflictException {
        LOGGER.trace("validateForUpdate({})", chore);
        List<String> errors = new ArrayList<>();
        if (chore.getId() == null) {
            errors.add("The id must not be null");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Not valid data", errors);
        }
    }

    private static boolean isNotNumeric(String str) {
        String regexNumeric = "-?\\d*(\\.\\d+)?";

        return !Pattern.matches(regexNumeric, str);
    }

    private static boolean isValidInteger(String valueString) {
        String regex = "^-?\\d+$";

        return Pattern.matches(regex, valueString);
    }
}
