package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;


public interface SharedFlatService {
    /**
     * Create a shared flat.
     *
     * @param wgDetailDto The shared flat to be created
     * @return WgDetailDto representing the created shared flat
     * @throws Exception if an error occurs during the creation process
     */
    WgDetailDto create(WgDetailDto wgDetailDto) throws ConflictException, ValidationException;

    /**
     * Log in to a shared flat.
     *
     * @param wgDetailDto The shared flat details for login
     * @return WgDetailDto representing the logged-in shared flat
     */
    WgDetailDto loginWg(WgDetailDto wgDetailDto);

    /**
     * Delete a shared flat.
     *
     * @return WgDetailDto representing the deleted shared flat
     */
    WgDetailDto delete(Long id) throws AuthorizationException;
}

