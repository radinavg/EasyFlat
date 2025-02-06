package at.ac.tuwien.sepr.groupphase.backend.service.impl.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeSuggestionDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Set;

@Component
public class UserValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Validator validator;


    public UserValidator(Validator validator) {
        this.validator = validator;
    }

    public void validateForRegister(UserDetailDto userDetailDto) throws ValidationException {
        LOGGER.trace("validateForRegister({})", userDetailDto);
        Set<ConstraintViolation<UserDetailDto>> validationViolations = validator.validate(userDetailDto);
        if (!validationViolations.isEmpty()) {
            throw new ValidationException("The data is not valid: ", validationViolations.stream().map(ConstraintViolation::getMessage).toList());
        }

    }

    public void validateForUpdate(UserDetailDto userDetailDto) throws ValidationException {
        LOGGER.trace("checkValidationForUpdate({})", userDetailDto);

        Set<ConstraintViolation<UserDetailDto>> validationViolations = validator.validate(userDetailDto);
        if (!validationViolations.isEmpty()) {
            throw new ValidationException("The data is not valid: ", validationViolations.stream().map(ConstraintViolation::getMessage).toList());
        }
    }

    public void validateForLogIn(UserLoginDto userLoginDto) throws ValidationException {
        LOGGER.trace("validateForLogIn({})", userLoginDto);

        Set<ConstraintViolation<UserLoginDto>> validationViolations = validator.validate(userLoginDto);
        if (!validationViolations.isEmpty()) {
            throw new ValidationException("The data is not valid: ", validationViolations.stream().map(ConstraintViolation::getMessage).toList());
        }
    }
}
