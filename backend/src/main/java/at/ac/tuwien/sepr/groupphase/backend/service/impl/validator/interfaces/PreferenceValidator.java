package at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.interfaces;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PreferenceDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Chore;
import at.ac.tuwien.sepr.groupphase.backend.entity.Preference;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

public interface PreferenceValidator {
    /**
     * Validates a PreferenceDto object for update, ensuring it meets necessary criteria.
     * This method checks if the provided PreferenceDto is valid for updating an existing Preference entity.
     *
     * @param preference The PreferenceDto object to be validated.
     */
    void validateForUpdate(PreferenceDto preference) throws ValidationException;
}
