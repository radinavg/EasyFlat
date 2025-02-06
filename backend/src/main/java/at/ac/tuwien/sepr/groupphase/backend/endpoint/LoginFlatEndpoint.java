package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.service.SharedFlatService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomUserDetailService;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/wgLogin")
public class LoginFlatEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final SharedFlatService sharedFlatService;

    private final CustomUserDetailService customUserDetailService;

    @Autowired
    public LoginFlatEndpoint(SharedFlatService sharedFlatService, CustomUserDetailService customUserDetailService) {
        this.sharedFlatService = sharedFlatService;
        this.customUserDetailService = customUserDetailService;
    }

    @PostMapping
    @Secured("ROLE_USER")
    public WgDetailDto loginWg(@RequestBody WgDetailDto wgDetailDto) {
        LOGGER.trace("loginWg({})", wgDetailDto);
        return sharedFlatService.loginWg(wgDetailDto);
    }

    @DeleteMapping("/{id}")
    public WgDetailDto deleteSharedFlat(@PathVariable Long id) throws AuthorizationException {
        LOGGER.trace("delete()");
        return sharedFlatService.delete(id);
    }


}
