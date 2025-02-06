package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpenFoodFactsItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.openfoodfactsapi.OpenFoodFactsAdjustmentsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.openfoodfactsapi.OpenFoodFactsCategoryPropertiesDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.openfoodfactsapi.OpenFoodFactsEcoscoreDataDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.openfoodfactsapi.OpenFoodFactsIngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.openfoodfactsapi.OpenFoodFactsPackagingDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.openfoodfactsapi.OpenFoodFactsPackagingsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.openfoodfactsapi.OpenFoodFactsProductDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.openfoodfactsapi.OpenFoodFactsResponseDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.expectedOpenFoodFactsItemDto;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.g;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.invalidEan;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.validEan;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.validOpenFoodFactsIngredient;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class OpenFoodFactsServiceTest {

    @Autowired
    private OpenFoodFactsService openFoodFactsService;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    private ApplicationUser applicationUser;

    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        applicationUser = userRepository.findById(1L).orElseThrow();
        when(authService.getUserFromToken()).thenReturn(applicationUser);
    }

    @Test
    @DisplayName("Test if the data received from the mocked Open Food Facts API is the expected data")
    void givenValidEanReturnOpenFoodFactsItemFromAPI() throws Exception {
        // given
        mockAPIResponseForOpenFoodFacts();

        // when
        OpenFoodFactsItemDto result = openFoodFactsService.findByEan(validEan);

        // then
        assertAll(
            () -> assertThat(result.eanCode()).isEqualTo(expectedOpenFoodFactsItemDto.eanCode()),
            () -> assertThat(result.generalName()).isEqualTo(expectedOpenFoodFactsItemDto.generalName()),
            () -> assertThat(result.productName()).isEqualTo(expectedOpenFoodFactsItemDto.productName()),
            () -> assertThat(result.brand()).isEqualTo(expectedOpenFoodFactsItemDto.brand()),
            () -> assertThat(result.quantityTotal()).isEqualTo(expectedOpenFoodFactsItemDto.quantityTotal()),
            () -> assertThat(result.unit()).isEqualTo(expectedOpenFoodFactsItemDto.unit()),
            () -> assertThat(result.description()).isEqualTo(expectedOpenFoodFactsItemDto.description()),
            () -> assertThat(result.ingredients().get(0).name()).isEqualTo(expectedOpenFoodFactsItemDto.ingredients().get(0).name()),
            () -> assertThat(result.boughtAt()).isEqualTo(expectedOpenFoodFactsItemDto.boughtAt())
        );
    }

    @Test
    @DisplayName("Test if the not found exception is thrown if an invalid EAN is provided")
    void givenInValidEanReturnNotFoundException() {
        // given
        mockAPIResponseForOpenFoodFacts();

        // when + then
        assertThrows(NotFoundException.class, () -> openFoodFactsService.findByEan(invalidEan));
    }

    private void mockAPIResponseForOpenFoodFacts() {
        OpenFoodFactsResponseDto mockedResponse = getMockedOpenFoodFactsResponse();

        String expectedUrl = "https://world.openfoodfacts.org/api/v2/product/" + validEan;

        when(restTemplate.getForObject(
            eq(expectedUrl), eq(OpenFoodFactsResponseDto.class))
        ).thenReturn(mockedResponse);
    }

    private OpenFoodFactsResponseDto getMockedOpenFoodFactsResponse() {
        OpenFoodFactsIngredientDto ingredient = new OpenFoodFactsIngredientDto("Sucre");

        List<OpenFoodFactsIngredientDto> ingredients = List.of(ingredient);

        OpenFoodFactsPackagingsDto packagings = new OpenFoodFactsPackagingsDto("g");

        OpenFoodFactsPackagingDto packaging = new OpenFoodFactsPackagingDto(Collections.singletonList(packagings));

        OpenFoodFactsAdjustmentsDto adjustments = new OpenFoodFactsAdjustmentsDto(packaging);

        OpenFoodFactsEcoscoreDataDto ecoscoreData = new OpenFoodFactsEcoscoreDataDto(adjustments);

        OpenFoodFactsCategoryPropertiesDto categoryProperties = new OpenFoodFactsCategoryPropertiesDto("Chocolate spread with hazelnuts");

        OpenFoodFactsProductDto product = new OpenFoodFactsProductDto(
            "Pâte à tartiner aux noisettes et au cacao",
            "Nuss-Nougat-Creme",
            "",
            "Nutella",
            "Nutella",
            "Nutella",
            "Ferrero",
            400L, // product quantity
            ecoscoreData,
            categoryProperties,
            ingredients,
            "Hermes,Kyrmes,Carrefour"
        );

        return new OpenFoodFactsResponseDto("3017620422003", product, true);
    }

    private OpenFoodFactsItemDto getExpectedOpenFoodFactsItemDto() {

        return new OpenFoodFactsItemDto(
            "3017620422003",
            "Pâte à tartiner aux noisettes et au cacao",
            "Nutella",
            "Ferrero",
            400L,
            g,
            "Chocolate spread with hazelnuts",
            "Hermes,Kyrmes,Carrefour",
            List.of(validOpenFoodFactsIngredient)
        );
    }
}
