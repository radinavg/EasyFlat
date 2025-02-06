package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.IngredientRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomUserDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class IngredientServiceImplTest {

    @Autowired
    private IngredientService service;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private TestDataGenerator testDataGenerator;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private CustomUserDetailService customUserDetailService;


    private ApplicationUser applicationUser;

    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        testDataGenerator.cleanUp();

        applicationUser = userRepository.findById(1L).orElseThrow();
        when(customUserDetailService.getUser(any(String.class))).thenReturn(applicationUser);
    }

    @Test
    @DisplayName("Given valid ingredient when create then ingredient is created")
    void givenValidIngredientWhenCreateAllThenIngredientIsCreated() throws ConflictException {
        // given
        IngredientDto ingredient1 = IngredientDtoBuilder.builder()
                .name("Ingredient 1")
                .build();

        IngredientDto ingredient2 = IngredientDtoBuilder.builder()
                .name("Ingredient 2")
                .build();


        // when
        List<Ingredient> createdIngredient = service.createAll(List.of(ingredient1, ingredient2));

        // then
        List<Ingredient> actual = ingredientRepository.findAllById(createdIngredient.stream()
                .map(Ingredient::getIngrId)
                .toList()
        );

        assertThat(actual).isNotNull();
        assertThat(actual).containsExactlyElementsOf(createdIngredient);
        assertThat(actual.stream()
                .map(Ingredient::getTitle)
        ).containsAll(
                List.of(ingredient1.name(), ingredient2.name())
        );
    }

    @Test
    @DisplayName("Given valid ingredient when create then ingredient is created")
    void givenTitleWhenFindByTitleThenAllItemsWithMatchingTitleAreReturned() {
        // given
        List<String> title = List.of("Ingredient 1", "Ingredient 2");


        // when
        List<Ingredient> ingredients = service.findByTitle(title);

        // then
        assertThat(ingredients).isNotEmpty();
        assertThat(ingredients).extracting(Ingredient::getTitle).containsExactlyInAnyOrderElementsOf(title);
    }

    @Test
    @DisplayName("Given valid ingredient when create then ingredient is created")
    void givenTitleWhichDoesNotExistsWhenFindByTitleThenAllItemsWithMatchingTitleAreReturned() {
        // given
        List<String> title = List.of("Ingredient 1", "DoesNotExists");


        // when
        List<Ingredient> ingredients = service.findByTitle(title);

        // then
        assertThat(ingredients).hasSize(1);
    }
}