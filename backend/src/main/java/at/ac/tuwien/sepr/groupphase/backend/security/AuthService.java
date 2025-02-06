package at.ac.tuwien.sepr.groupphase.backend.security;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;

public interface AuthService {

    /**
     * Retrieves the ApplicationUser associated with the current authentication token.
     *
     * @return The ApplicationUser associated with the token.
     */
    ApplicationUser getUserFromToken();
}
