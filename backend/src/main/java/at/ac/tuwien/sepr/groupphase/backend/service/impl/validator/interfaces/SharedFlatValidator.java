package at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.interfaces;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

public interface SharedFlatValidator {

    /**
     * Validates a SharedFlat for creation, checking both general data validation and conflicts with existing data.
     *
     * @param wgDetailDto The SharedFlat to be validated for creation.
     * @throws ConflictException   If there is a conflict with persisted data.
     * @throws ValidationException If the provided data is not valid.
     */
    void validateForCreate(WgDetailDto wgDetailDto) throws ConflictException, ValidationException;

}
