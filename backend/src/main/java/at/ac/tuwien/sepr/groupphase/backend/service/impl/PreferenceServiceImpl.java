package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PreferenceDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.PreferenceMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Preference;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.PreferenceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.service.PreferenceService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.authorization.Authorization;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.interfaces.PreferenceValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

@Service
public class PreferenceServiceImpl implements PreferenceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final AuthService authService;

    private final PreferenceRepository preferenceRepository;

    private final PreferenceMapper preferenceMapper;

    private final PreferenceValidator preferenceValidator;

    private final UserRepository userRepository;

    private final Authorization authorization;


    public PreferenceServiceImpl(AuthService authService, PreferenceRepository preferenceRepository, PreferenceMapper preferenceMapper, PreferenceValidator preferenceValidator, UserRepository userRepository, Authorization authorization) {
        this.authService = authService;
        this.preferenceRepository = preferenceRepository;
        this.preferenceMapper = preferenceMapper;
        this.preferenceValidator = preferenceValidator;
        this.userRepository = userRepository;
        this.authorization = authorization;
    }

    @Override
    public PreferenceDto update(PreferenceDto preferenceDto) throws ValidationException {
        LOGGER.trace("update({})", preferenceDto);
        preferenceValidator.validateForUpdate(preferenceDto);
        ApplicationUser applicationUser = authService.getUserFromToken();

        Preference preference = preferenceMapper.preferenceDtoToEntity(preferenceDto);
        preference.setUserId(applicationUser);

        Optional<Preference> existingPreference = Optional.ofNullable(preferenceRepository.findByUserId(applicationUser));

        if (existingPreference.isPresent()) {
            Preference existing = existingPreference.get();
            existing.setFirst(preference.getFirst());
            existing.setSecond(preference.getSecond());
            existing.setThird(preference.getThird());
            existing.setFourth(preference.getFourth());

            Preference toSave = preferenceRepository.save(existing);
            applicationUser.setPreference(existing);
            userRepository.save(applicationUser);
            return preferenceMapper.entityToPreferenceDto(toSave);
        } else {
            Preference toSave = preferenceRepository.save(preference);
            applicationUser.setPreference(preference);
            userRepository.save(applicationUser);
            return preferenceMapper.entityToPreferenceDto(toSave);
        }
    }

    @Override
    public PreferenceDto getLastPreference() {
        LOGGER.trace("getLastPreference()");
        ApplicationUser applicationUser = authService.getUserFromToken();
        Preference preferenceToRet = preferenceRepository.findByUserId(applicationUser);
        return preferenceMapper.entityToPreferenceDto(preferenceToRet);
    }

}
