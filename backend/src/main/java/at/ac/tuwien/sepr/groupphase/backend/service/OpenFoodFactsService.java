package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpenFoodFactsItemDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.client.RestClientException;

public interface OpenFoodFactsService {

    /**
     * Sends a request to the OFF API, extracts the content and creates & returns an {@link OpenFoodFactsItemDto}.
     *
     * @param ean the EAN code that should be searched for in the API
     * @return an object of type {@link OpenFoodFactsItemDto} constructed from the API response
     */
    OpenFoodFactsItemDto findByEan(Long ean) throws ConflictException, RestClientException, JsonProcessingException;
}
