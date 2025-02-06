package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeIngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeIngredientDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeSuggestionDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeSuggestionDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UnitMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.service.CookingService;
import at.ac.tuwien.sepr.groupphase.backend.service.DigitalStorageService;
import at.ac.tuwien.sepr.groupphase.backend.service.UnitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class CookingDigitalStorageIntegrationTest {

    @Autowired
    private DigitalStorageService digitalStorageService;

    @Autowired
    private CookingService cookingService;

    @Autowired
    private TestDataGenerator testDataGenerator;

    @Autowired
    private ItemRepository itemRepositoryAutowired;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UnitService unitService;

    @Autowired
    private UnitMapper unitMapper;

    @MockBean
    private AuthService authService;

    private ApplicationUser applicationUser;

    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        testDataGenerator.cleanUp();
        applicationUser = userRepository.findById(1L).orElseThrow();
        when(authService.getUserFromToken()).thenReturn(applicationUser);

    }

    @Test
    void testCookRecipeRemoveItemsQuantityFromStorage() throws ValidationException, ConflictException, AuthenticationException, AuthorizationException {
        // given
        RecipeSuggestionDto recipeToCook = getRecipeToCook();

        List<DigitalStorageItem> itemsFromDigitalStorageList = itemRepositoryAutowired.findAll();

        // when
        RecipeSuggestionDto result = cookingService.cookRecipe(recipeToCook);

        List<DigitalStorageItem> itemsFromDigitalStorageAfterCookList = itemRepositoryAutowired.findAllByDigitalStorage_StorageId(1L);

        Map<String, Double> digitalStorageItemsMap = new HashMap<>();
        Map<String, Double> recipeIngredients = new HashMap<>();
        Map<String, Double> digitalStorageItemsAfterCooking = new HashMap<>();

        for (DigitalStorageItem digitalStorageItem : itemsFromDigitalStorageList) {
            String itemName = digitalStorageItem.getItemCache().getProductName();
            Double currentQuantity = unitService.convertUnits(digitalStorageItem.getItemCache().getUnit(), unitService.getMinUnit(digitalStorageItem.getItemCache().getUnit()), digitalStorageItem.getQuantityCurrent());


            digitalStorageItemsMap.merge(itemName, currentQuantity, Double::sum);
        }
        for (RecipeIngredientDto recipeIngredientDto : recipeToCook.extendedIngredients()) {
            Double currentQuantity = unitService.convertUnits(unitMapper.unitDtoToEntity(recipeIngredientDto.unitEnum()), unitService.getMinUnit(unitMapper.unitDtoToEntity(recipeIngredientDto.unitEnum())), recipeIngredientDto.amount());
            recipeIngredients.put(recipeIngredientDto.name(), currentQuantity);
        }


        for (DigitalStorageItem digitalStorageItem : itemsFromDigitalStorageAfterCookList) {
            String itemName = digitalStorageItem.getItemCache().getProductName();
            Double currentQuantity = unitService.convertUnits(digitalStorageItem.getItemCache().getUnit(), unitService.getMinUnit(digitalStorageItem.getItemCache().getUnit()), digitalStorageItem.getQuantityCurrent());


            digitalStorageItemsAfterCooking.merge(itemName, currentQuantity, Double::sum);
        }


        for (Map.Entry<String, Double> recipeIngredient : recipeIngredients.entrySet()) {
            String recipeIngredientName = recipeIngredient.getKey();
            if (digitalStorageItemsMap.get(recipeIngredientName) != null) {
                Double recipeIngredientQuantity = recipeIngredient.getValue();
                Double quantityBeforeCooking = digitalStorageItemsMap.get(recipeIngredientName);
                Double quantityAfterCooking = digitalStorageItemsAfterCooking.get(recipeIngredientName);
                if (quantityBeforeCooking != null && quantityAfterCooking != null) {
                    assertThat(recipeIngredientQuantity).isEqualTo(quantityBeforeCooking - quantityAfterCooking);
                }
            }
        }

    }

    @Test
    @DisplayName("Cooking Invalid Recipe Should Throw ValidationException")
    void testCookInvalidRecipe() {
        // given
        RecipeSuggestionDto invalidRecipe = RecipeSuggestionDtoBuilder.builder()
            .id(1L)
            .title("")  // Invalid: Blank title
            .servings(-5)  // Invalid: Negative servings
            .readyInMinutes(10)
            .extendedIngredients(Arrays.asList(
                RecipeIngredientDtoBuilder.builder()
                    .id(1L)
                    .name("Invalid Ingredient")
                    .unit("kg")
                    .amount(1.0)
                    .build()))
            .summary("How to cook invalid recipe")
            .build();

        // when/then
        assertThrows(ValidationException.class, () -> cookingService.cookRecipe(invalidRecipe));
    }

    private RecipeSuggestionDto getRecipeToCook() {
        Set<UnitDto> subUnit = new HashSet<>();
        subUnit.add(new UnitDto("g", null, null));
        RecipeSuggestionDto testRecipe = RecipeSuggestionDtoBuilder.builder()
            .id(1L)
            .title("Test recipe")
            .servings(5)
            .readyInMinutes(10)
            .extendedIngredients(Arrays.asList(
                RecipeIngredientDtoBuilder.builder()
                    .id(1L)
                    .name("Granny Smith Apples")
                    .unit("kg")
                    .unitEnum(UnitDtoBuilder.builder()
                        .name("pcs")
                        .convertFactor(1L)
                        .build())
                    .amount(0.5)
                    .build(),
                RecipeIngredientDtoBuilder.builder()
                    .id(2L)
                    .name("Alpro Vanille - 500 g")
                    .unit("g")
                    .unitEnum(UnitDtoBuilder.builder()
                        .name("kg")
                        .convertFactor(1000L)
                        .subUnit(subUnit)
                        .build())
                    .amount(0.2)
                    .build(),
                RecipeIngredientDtoBuilder.builder()
                    .id(3L)
                    .name("sugar")
                    .unit("kg")
                    .unitEnum(UnitDtoBuilder.builder()
                        .name("kg")
                        .convertFactor(1000L)
                        .subUnit(subUnit)
                        .build())
                    .amount(0.2)
                    .build()))
            .summary("How to cook")
            .build();
        return testRecipe;
    }
}
