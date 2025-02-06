package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PreferenceDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Preference;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

public interface PreferenceService {
    PreferenceDto update(PreferenceDto preferenceDto) throws AuthenticationException, ValidationException;

    PreferenceDto getLastPreference() throws AuthenticationException;
}
