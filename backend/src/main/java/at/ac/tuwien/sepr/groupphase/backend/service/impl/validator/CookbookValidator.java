package at.ac.tuwien.sepr.groupphase.backend.service.impl.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.CookbookDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.CookbookMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Cookbook;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeSuggestion;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.CookbookRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
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
public class CookbookValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Validator validator;
    private final CookbookRepository cookbookRepository;

    private final CookbookMapper cookbookMapper;
    private final AuthService authService;


    public CookbookValidator(Validator validator, CookbookRepository cookbookRepository, CookbookMapper cookbookMapper, AuthService authService) {
        this.validator = validator;
        this.cookbookRepository = cookbookRepository;
        this.cookbookMapper = cookbookMapper;
        this.authService = authService;
    }

    public void validateForCreate(CookbookDto cookbookDto) throws ConflictException, ValidationException {
        LOGGER.trace("validateForCreate({})", cookbookDto);

        checkForDataValidation(cookbookDto);
        checkCookbookForCreate(cookbookDto);
    }

    private void checkForDataValidation(CookbookDto cookbookDto) throws ValidationException {
        LOGGER.trace("checkForDataValidation({})", cookbookDto);

        Set<ConstraintViolation<CookbookDto>> validationViolations = validator.validate(cookbookDto);
        if (!validationViolations.isEmpty()) {
            throw new ValidationException("The data is not valid", validationViolations.stream().map(ConstraintViolation::getMessage).toList());
        }
    }

    private void checkCookbookForCreate(CookbookDto cookbookDto) throws ConflictException {
        LOGGER.trace("checkCookbookForCreate({})", cookbookDto);
        List<String> errors = new ArrayList<>();

        if (cookbookDto.id() != null) {
            errors.add("The Id must be null");
        }
        if (!errors.isEmpty()) {
            throw new ConflictException("Conflict with other data", errors);
        }
    }
}
