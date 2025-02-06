package at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.interfaces;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface ShoppingItemValidator {

    /**
     * Validates a ShoppingItemDto for creation, checking both general data validation and conflicts with existing data.
     *
     * @param itemDto           The ShoppingItemDto to be validated for creation.
     * @param shoppingLists     List of existing ShoppingLists to check for associations.
     * @param digitalStorageList List of existing DigitalStorages to check for associations.
     * @param unitList          List of existing Units to validate against the item's unit.
     * @throws ConflictException   If there is a conflict with existing data.
     * @throws ValidationException If the provided data is not valid.
     */
    void validateForCreate(ShoppingItemDto itemDto,
                                  List<ShoppingList> shoppingLists,
                                  List<DigitalStorage> digitalStorageList,
                                  List<Unit> unitList) throws ConflictException, ValidationException;

    /**
     * Validates a ShoppingItemDto for update, checking both general data validation and conflicts with existing data.
     *
     * @param itemDto           The ShoppingItemDto to be validated for update.
     * @param shoppingLists     List of existing ShoppingLists to check for associations.
     * @param digitalStorageList List of existing DigitalStorages to check for associations.
     * @param unitList          List of existing Units to validate against the item's unit.
     * @throws ConflictException   If there is a conflict with existing data.
     * @throws ValidationException If the provided data is not valid.
     */
    void validateForUpdate(ShoppingItemDto itemDto,
                                  List<ShoppingList> shoppingLists,
                                  List<DigitalStorage> digitalStorageList,
                                  List<Unit> unitList) throws ConflictException, ValidationException;
}
