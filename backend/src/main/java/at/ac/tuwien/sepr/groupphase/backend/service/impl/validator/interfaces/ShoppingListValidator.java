package at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.interfaces;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

public interface ShoppingListValidator {

    /**
     * Validates a ShoppingList for creation, checking both general data validation and conflicts with existing data.
     *
     * @param shoppingList The ShoppingListDto to be validated for creation.
     * @throws ConflictException   If there is a conflict with persisted data.
     * @throws ValidationException If the provided data is not valid.
     */
    void validateForCreate(ShoppingListDto shoppingList) throws ConflictException, ValidationException;
}

