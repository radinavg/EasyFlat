package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AlternativeNameDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.AlternativeName;
import org.springframework.stereotype.Service;

@Service
public interface AlternativeNameService {
    /**
     * Creates a new alternative name.
     *
     * @param alternativeNameDto The DTO containing alternative name information.
     * @return The created AlternativeName entity.
     */
    AlternativeName create(AlternativeNameDto alternativeNameDto);

    /**
     * Creates a new alternative name if it does not already exist.
     *
     * @param alternativeNameDto The DTO containing alternative name information.
     * @return The created AlternativeName entity, or null if it already exists.
     */
    AlternativeName createIfNotExist(AlternativeNameDto alternativeNameDto);

    /**
     * Retrieves an alternative name by its ID.
     *
     * @param id The ID of the alternative name to retrieve.
     * @return The AlternativeName entity, or null if not found.
     */
    AlternativeName findById(Long id);
}
