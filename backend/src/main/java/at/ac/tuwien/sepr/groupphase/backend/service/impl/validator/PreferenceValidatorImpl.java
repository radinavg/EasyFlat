package at.ac.tuwien.sepr.groupphase.backend.service.impl.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PreferenceDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.interfaces.PreferenceValidator;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
public class PreferenceValidatorImpl implements PreferenceValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Validator validator;

    public PreferenceValidatorImpl(Validator validator) {
        this.validator = validator;
    }

    @Override
    public void validateForUpdate(PreferenceDto preference) throws ValidationException {
        LOGGER.trace("validateForUpdate({})", preference);
        List<String> errors = new ArrayList<>();

        if (preference.fourth() != null && (preference.third() == null || preference.second() == null || preference.first() == null)) {
            errors.add("Chores must be provided in the order of first to fourth");
        } else if (preference.third() != null && (preference.second() == null || preference.first() == null)) {
            errors.add("Chores must be provided in the order of first to fourth");
        } else if (preference.second() != null && preference.first() == null) {
            errors.add("Chores must be provided in the order of first to fourth");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Not valid data", errors);
        }

    }
}
