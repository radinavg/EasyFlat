package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpenFoodFactsItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.openfoodfactsapi.OpenFoodFactsResponseDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemFromOpenFoodFactsApiMapper;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.OpenFoodFactsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.lang.invoke.MethodHandles;

@Service
public class OpenFoodFactsServiceImpl implements OpenFoodFactsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final RestTemplate restTemplate;
    private final ItemFromOpenFoodFactsApiMapper itemFromApiMapper;
    private final String openFoodFactsApi = "https://world.openfoodfacts.org/api/v2/product/";

    public OpenFoodFactsServiceImpl(RestTemplate restTemplate,
                                    ItemFromOpenFoodFactsApiMapper itemFromApiMapper) {
        this.restTemplate = restTemplate;
        this.itemFromApiMapper = itemFromApiMapper;
    }

    @Override
    public OpenFoodFactsItemDto findByEan(Long ean) throws ConflictException {
        LOGGER.trace("findByEan({})", ean);

        String requestString = openFoodFactsApi + ean;

        try {
            // Get data from API using EAN code
            OpenFoodFactsResponseDto jsonResponse = restTemplate.getForObject(requestString, OpenFoodFactsResponseDto.class);

            if (jsonResponse == null || !jsonResponse.status()) {
                throw new NotFoundException("Product with EAN " + ean + " not found");
            }

            // Map the data to an ItemDto
            return itemFromApiMapper.mapFromJsonNode(jsonResponse);
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NotFoundException("Product with EAN " + ean + " not found");
            } else {
                throw new NotFoundException("An error occurred while trying to process the EAN");
            }
        }
    }
}
