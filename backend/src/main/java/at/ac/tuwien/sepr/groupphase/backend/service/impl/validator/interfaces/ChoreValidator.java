package at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.interfaces;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChoreDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Chore;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

public interface ChoreValidator {

    /**
     * Validates a ChoreDto object for creation, ensuring it meets necessary criteria.
     * This method checks if the provided ChoreDto is valid for creating a new Chore entity.
     *
     * @param chore The ChoreDto object to be validated.
     * @throws ValidationException Thrown if the provided ChoreDto does not meet validation rules.
     * @throws ConflictException  Thrown if there is a conflict with existing data preventing creation.
     */
    void validateForCreate(ChoreDto chore) throws ValidationException, ConflictException;

    /**
     * Validates a ChoreDto object for update, ensuring it meets necessary criteria.
     * This method checks if the provided ChoreDto is valid for updating an existing Chore entity.
     *
     * @param chore The ChoreDto object to be validated.
     * @throws ValidationException Thrown if the provided ChoreDto does not meet validation rules.
     * @throws ConflictException  Thrown if there is a conflict with existing data preventing creation.
     */
    void validateForUpdate(Chore chore) throws ValidationException, ConflictException;

}
