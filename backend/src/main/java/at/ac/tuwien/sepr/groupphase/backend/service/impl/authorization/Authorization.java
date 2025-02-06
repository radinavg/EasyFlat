package at.ac.tuwien.sepr.groupphase.backend.service.impl.authorization;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Component
public class Authorization {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final AuthService authService;

    public Authorization(AuthService authService) {
        this.authService = authService;
    }

    public void authorizeUser(List<Long> allowedUser, String errorMessage) throws AuthorizationException {
        LOGGER.trace("authorizeUser({}, {})", id, errorMessage);

        ApplicationUser user = authService.getUserFromToken();
        if (user == null) {
            throw new AuthorizationException("Authorization failed", List.of("User does not exists"));
        }

        if (!allowedUser.contains(user.getId())) {
            throw new AuthorizationException("Authorization failed", List.of(errorMessage));
        }
    }

    public void authorizeUser(List<Long> id) throws AuthorizationException {
        LOGGER.trace("authorizeUser({})", id);

        authorizeUser(id, "User does not have access to this resource");
    }
}
