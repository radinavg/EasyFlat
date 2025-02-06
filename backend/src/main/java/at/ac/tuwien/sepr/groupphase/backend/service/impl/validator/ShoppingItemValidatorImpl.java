package at.ac.tuwien.sepr.groupphase.backend.service.impl.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.interfaces.ShoppingItemValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
public class ShoppingItemValidatorImpl implements ShoppingItemValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Validator validator;

    public ShoppingItemValidatorImpl(Validator validator) {
        this.validator = validator;
    }

    @Override
    public void validateForCreate(ShoppingItemDto itemDto,
                                  List<ShoppingList> shoppingLists,
                                  List<DigitalStorage> digitalStorageList,
                                  List<Unit> unitList) throws ConflictException, ValidationException {
        LOGGER.trace("validateForCreate({})", itemDto);

        checkValidationForCreate(itemDto);
        checkConflictForCreate(itemDto, shoppingLists, digitalStorageList, unitList);
    }

    private void checkValidationForCreate(ShoppingItemDto itemDto) throws ValidationException {
        LOGGER.trace("checkValidationForCreate({})", itemDto);

        Set<ConstraintViolation<ShoppingItemDto>> validationViolations = validator.validate(itemDto);
        if (!validationViolations.isEmpty()) {
            throw new ValidationException("The data is not valid", validationViolations.stream().map(ConstraintViolation::getMessage).toList());
        }
    }

    private void checkConflictForCreate(ShoppingItemDto itemDto, List<ShoppingList> shoppingLists, List<DigitalStorage> digitalStorageList, List<Unit> unitList) throws ConflictException {
        LOGGER.trace("checkConflictForCreate({}, {}, {})", itemDto, digitalStorageList, unitList);

        List<String> errors = new ArrayList<>();
        if (itemDto.itemId() != null) {
            errors.add("The Id must be null");
        }

        if (itemDto.shoppingList() == null) {
            errors.add("The item is not linked to a shopping list");
        } else {
            if (shoppingLists.stream()
                .map(ShoppingList::getId)
                .noneMatch(id ->
                    Objects.equals(id, itemDto.shoppingList().id())
                )
            ) {
                errors.add("The given shopping list does not exists");
            }
        }

        if (digitalStorageList.isEmpty()) {
            errors.add("The item is not linked to an digital storage");
        }

        if (unitList.stream().map(Unit::getName).noneMatch(name -> name.equals(itemDto.unit().name()))) {
            errors.add("The given Unit does not exists");
        }

        if (!errors.isEmpty()) {
            throw new ConflictException("There is a conflict with persisted data", errors);
        }
    }

    @Override
    public void validateForUpdate(ShoppingItemDto itemDto,
                                  List<ShoppingList> shoppingLists,
                                  List<DigitalStorage> digitalStorageList,
                                  List<Unit> unitList) throws ConflictException, ValidationException {
        LOGGER.trace("validateForUpdate({})", itemDto);

        checkValidationForUpdate(itemDto);
        checkConflictForUpdate(itemDto, shoppingLists, digitalStorageList, unitList);
    }

    private void checkValidationForUpdate(ShoppingItemDto itemDto) throws ValidationException {
        LOGGER.trace("checkValidationForUpdate({})", itemDto);

        Set<ConstraintViolation<ShoppingItemDto>> validationViolations = validator.validate(itemDto);
        if (!validationViolations.isEmpty()) {
            throw new ValidationException("The data is not valid", validationViolations.stream().map(ConstraintViolation::getMessage).toList());
        }
    }

    private void checkConflictForUpdate(ShoppingItemDto itemDto, List<ShoppingList> shoppingLists, List<DigitalStorage> digitalStorageList, List<Unit> unitList) throws ConflictException {
        LOGGER.trace("checkItemForUpdate({}, {}, {})", itemDto, digitalStorageList, unitList);

        List<String> errors = new ArrayList<>();
        if (itemDto.itemId() == null) {
            errors.add("The Id can not be null");
        }

        if (itemDto.shoppingList() == null) {
            errors.add("The item is not linked to a Shopping List");
        } else {
            if (shoppingLists.stream()
                .map(ShoppingList::getId)
                .noneMatch(id ->
                    Objects.equals(id, itemDto.shoppingList().id())
                )
            ) {
                errors.add("The given shopping list does not exists");
            }
        }

        if (digitalStorageList.isEmpty()) {
            errors.add("The item is not linked to a digital storage");
        }

        if (unitList.stream().map(Unit::getName).noneMatch(name -> name.equals(itemDto.unit().name()))) {
            errors.add("The given unit does not exists");
        }

        if (!errors.isEmpty()) {
            throw new ConflictException("There is a conflict with persisted data", errors);
        }
    }

}


