package at.ac.tuwien.sepr.groupphase.backend.service.impl.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemLabelDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.interfaces.ItemLabelValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Set;

@Component
public class ItemLabelValidatorImpl implements ItemLabelValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Validator validator;

    public ItemLabelValidatorImpl(Validator validator) {
        this.validator = validator;
    }

    @Override
    public void validate(ItemLabelDto itemLabelDto) throws ValidationException {
        LOGGER.trace("validateForCreate({})", itemLabelDto);

        checkValidation(itemLabelDto);
    }

    private void checkValidation(ItemLabelDto itemLabelDto) throws ValidationException {
        LOGGER.trace("checkValidationForCreate({})", itemLabelDto);
        Set<ConstraintViolation<ItemLabelDto>> validationViolations = validator.validate(itemLabelDto);
        if (!validationViolations.isEmpty()) {
            throw new ValidationException("Not valid data", validationViolations.stream().map(ConstraintViolation::getMessage).toList());
        }
    }
}
