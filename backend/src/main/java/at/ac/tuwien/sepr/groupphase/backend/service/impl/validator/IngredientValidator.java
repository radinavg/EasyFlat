package at.ac.tuwien.sepr.groupphase.backend.service.impl.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Component
public class IngredientValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private void checkIngredientForCreate(IngredientDto ingredientDto) throws ConflictException {
        LOGGER.trace("checkIngredientForCreate({})", ingredientDto);

        if (ingredientDto.ingredientId() != null) {
            throw new ConflictException("Conflict with other data", List.of("The Id of an ingredient must be null"));
        }
    }

    public void validateListForCreate(List<IngredientDto> ingredientDtoList) throws ConflictException {
        LOGGER.trace("checkIngredientListForCreate({})", ingredientDtoList);

        for (IngredientDto ingredientDto : ingredientDtoList) {
            checkIngredientForCreate(ingredientDto);
        }
    }
}
