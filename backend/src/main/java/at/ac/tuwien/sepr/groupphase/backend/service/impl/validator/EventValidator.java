package at.ac.tuwien.sepr.groupphase.backend.service.impl.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventLabelDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeIngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Component
public class EventValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Validator validator;

    public EventValidator(Validator validator) {
        this.validator = validator;
    }

    public void validate(EventDto event) throws ValidationException {
        LOGGER.trace("validateForCreate({})", event);

        autoCheck(event);
        validateLabels(event.labels());

    }


    private void autoCheck(EventDto event) throws ValidationException {
        Set<ConstraintViolation<EventDto>> validationViolations = validator.validate(event);
        List<String> errors = new ArrayList<>(validationViolations.size());
        for (ConstraintViolation<EventDto> violation : validationViolations) {
            errors.add(violation.getMessage());
        }
        if (event.labels() != null) {
            if (event.labels().size() > 3) {
                errors.add("You cannot add more thant 3 labels");
            }

            if (!event.labels().isEmpty()) {
                for (EventLabelDto label : event.labels()) {
                    if (label.labelName().length() > 9) {
                        errors.add("Label name " + label.labelName() + " should be shorter (maximum of 9 chars)");
                    }
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new ValidationException("Data is not valid", errors);
        }
    }

    public void validateLabels(List<EventLabelDto> labels) throws ValidationException {
        LOGGER.trace("validateLabels({})", labels);
        if (labels != null) {
            for (EventLabelDto label : labels) {
                Set<ConstraintViolation<EventLabelDto>> validationViolations = validator.validate(label);
                if (!validationViolations.isEmpty()) {
                    throw new ValidationException("Ingredient data is not valid: ", validationViolations.stream().map(ConstraintViolation::getMessage).toList());
                }
            }
        }
    }


}
