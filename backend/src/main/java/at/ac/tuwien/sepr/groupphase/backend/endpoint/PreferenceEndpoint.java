package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PreferenceDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.PreferenceMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Preference;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.ChoreService;
import at.ac.tuwien.sepr.groupphase.backend.service.PreferenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/chores/preference")
public class PreferenceEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ChoreService choreService;
    private final PreferenceService preferenceService;

    private final PreferenceMapper preferenceMapper;

    @Autowired
    public PreferenceEndpoint(ChoreService choreService, PreferenceService preferenceService, PreferenceMapper preferenceMapper) {
        this.choreService = choreService;
        this.preferenceService = preferenceService;
        this.preferenceMapper = preferenceMapper;
    }

    @Secured("ROLE_USER")
    @PutMapping()
    public PreferenceDto updatePreference(@RequestBody PreferenceDto preferenceDto) throws AuthenticationException, ValidationException {
        LOGGER.info("updatePreference({})", preferenceDto);
        return preferenceService.update(preferenceDto);
    }

    @Secured("ROLE_USER")
    @GetMapping
    public PreferenceDto getLastPreference() throws AuthenticationException {
        LOGGER.trace("getLastPreference()");
        return preferenceService.getLastPreference();
    }
}