package at.ac.tuwien.sepr.groupphase.backend.service.impl.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.interfaces.SharedFlatValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class SharedFlatValidatorImpl implements SharedFlatValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Validator validator;

    public SharedFlatValidatorImpl(Validator validator) {
        this.validator = validator;
    }

    public void validateForCreate(WgDetailDto sharedFlat) throws ConflictException, ValidationException {
        LOGGER.trace("validateForCreate({})", sharedFlat);

        checkValidationForCreate(sharedFlat);
        checkConflictForCreate(sharedFlat);
    }

    private void checkValidationForCreate(WgDetailDto sharedFlat) throws ValidationException {
        LOGGER.trace("checkValidationForCreate({})", sharedFlat);

        Set<ConstraintViolation<WgDetailDto>> validationViolations = validator.validate(sharedFlat);
        if (!validationViolations.isEmpty()) {
            throw new ValidationException("The data is not valid", validationViolations.stream().map(ConstraintViolation::getMessage).toList());
        }
    }

    private void checkConflictForCreate(WgDetailDto sharedFlat) throws ConflictException {
        LOGGER.trace("checkConflictForCreate({})", sharedFlat);

        List<String> errors = new ArrayList<>();

        if (sharedFlat.getName() == null) {
            errors.add("No name given");
        } else {
            if (sharedFlat.getName().isBlank()) {
                errors.add("Flat name can not be blank");
            }
            if (sharedFlat.getName().length() > 200) {
                errors.add("Flat name is too long");
            }
        }
        if (sharedFlat.getPassword() == null) {
            errors.add("Password is not given");
        }
        if (sharedFlat.getPassword().length() > 30) {
            errors.add("Password is too long");
        }
        if (sharedFlat.getId() != null) {
            errors.add("The Id must be null");
        }

        if (!errors.isEmpty()) {
            throw new ConflictException("There is a conflict with persisted data", errors);
        }
    }

}
