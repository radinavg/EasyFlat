package at.ac.tuwien.sepr.groupphase.backend.service.impl.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Set;

@Component
public class DigitalStorageValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Validator validator;

    public DigitalStorageValidator(Validator validator) {
        this.validator = validator;
    }

    public void validateForCreate(DigitalStorageDto digitalStorageDto) throws ConflictException, ValidationException {
        LOGGER.trace("validateForCreate({})", digitalStorageDto);

        checkForDataValidation(digitalStorageDto);
        checkDigitalStorageForCreate(digitalStorageDto);
    }

    public void validateForSearchItems(ItemSearchDto itemSearchDto) throws ValidationException {
        LOGGER.trace("validateForSearchItems({})", itemSearchDto);
        Set<ConstraintViolation<ItemSearchDto>> validationViolations = validator.validate(itemSearchDto);
        if (!validationViolations.isEmpty()) {
            throw new ValidationException("Search Data is not valid", validationViolations.stream().map(ConstraintViolation::getMessage).toList());
        }
    }

    private void checkForDataValidation(DigitalStorageDto digitalStorageDto) throws ValidationException {
        LOGGER.trace("checkForDataValidation({})", digitalStorageDto);

        Set<ConstraintViolation<DigitalStorageDto>> validationViolations = validator.validate(digitalStorageDto);
        if (!validationViolations.isEmpty()) {
            throw new ValidationException("The data is not valid", validationViolations.stream().map(ConstraintViolation::getMessage).toList());
        }
    }

    private void checkDigitalStorageForCreate(DigitalStorageDto digitalStorageDto) throws ConflictException {
        LOGGER.trace("checkDigitalStorageForCreate({})", digitalStorageDto);


        if (digitalStorageDto.storageId() != null) {
            throw new ConflictException("Conflict with other data", List.of("The Id must be null"));
        }
    }

}

