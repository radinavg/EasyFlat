package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.repository.RecipeIngredientRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeIngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeIngredientDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeSuggestionDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeSuggestionDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.RecipeIngredientMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeIngredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UnitRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CookingEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    private final String BASE_URI = "/api/v1/cooking";

    private final String COOKBOOK_BASE_URI = "/api/v1/cooking/cookbook";

    @Autowired
    private TestDataGenerator testDataGenerator;

    @Autowired
    private UserRepository userRepository;

    private ApplicationUser applicationUser;

    @MockBean
    private AuthService authService;

    @Autowired
    private RecipeIngredientMapper ingredientMapper;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private RecipeIngredientRepository recipeIngredientRepository;

    @MockBean
    private RestTemplate restTemplate;

    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        testDataGenerator.cleanUp();
        applicationUser = userRepository.findById(1L).orElseThrow();
        when(authService.getUserFromToken()).thenReturn(applicationUser);
    }

    @Test
    void testCookRecipeEndpoint() throws Exception {
        // given
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
                    .name("apples")
                    .unit("kg")
                    .unitEnum(UnitDtoBuilder.builder()
                        .name("kg")
                        .convertFactor(1000L)
                        .subUnit(subUnit)
                        .build())
                    .amount(1.0)
                    .build(),
                RecipeIngredientDtoBuilder.builder()
                    .id(2L)
                    .name("flour")
                    .unit("kg")
                    .unitEnum(UnitDtoBuilder.builder()
                        .name("kg")
                        .convertFactor(1000L)
                        .subUnit(subUnit)
                        .build())
                    .amount(0.5)
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


        String body = objectMapper.writeValueAsString(testRecipe);

        // when
        MvcResult mvcResult = this.mockMvc.perform(put(BASE_URI + "/cook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        // then
        //  mvcResult.getResponse().getContentAsString(); // You can assert the response content as needed

        RecipeSuggestionDto responseRecipe = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), RecipeSuggestionDto.class);
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
            () -> assertNotNull(mvcResult.getResponse()),
            () -> assertEquals(testRecipe, responseRecipe)
        );
    }

    @Test
    void cookingInvalidRecipeThrowValidationException() throws Exception {
        Set<UnitDto> subUnit = new HashSet<>();
        subUnit.add(new UnitDto("g", null, null));
        RecipeSuggestionDto invalidRecipe = RecipeSuggestionDtoBuilder.builder()
            .id(1L)
            .title("")  // Invalid, empty title
            .servings(5)
            .readyInMinutes(10)
            .extendedIngredients(Arrays.asList(
                RecipeIngredientDtoBuilder.builder()
                    .id(1L)
                    .name("apples")
                    .unit("kg")
                    .unitEnum(UnitDtoBuilder.builder()
                        .name("kg")
                        .convertFactor(1000L)
                        .subUnit(subUnit)
                        .build())
                    .amount(1.0)
                    .build(),
                RecipeIngredientDtoBuilder.builder()
                    .id(2L)
                    .name("flour")
                    .unit("kg")
                    .unitEnum(UnitDtoBuilder.builder()
                        .name("kg")
                        .convertFactor(1000L)
                        .subUnit(subUnit)
                        .build())
                    .amount(0.5)
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

        String body = objectMapper.writeValueAsString(invalidRecipe);

        // when and then
        MvcResult mvcResult = this.mockMvc.perform(put(BASE_URI + "/cook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

    @Test
    void testGetRecipeSuggestions() throws Exception {
        // given

        mockAPIResponse();
        // when
        MvcResult mvcResult = this.mockMvc.perform(get(BASE_URI)
                .param("type", "")
                .header(HttpHeaders.AUTHORIZATION, jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        // then
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());


    }

    @Test
    @DisplayName("Positive test for creating a recipe with status 200")
    void createValidRecipeShouldReturnStatus200() throws Exception {
        RecipeSuggestionDto recipe = RecipeSuggestionDtoBuilder.builder()
            .title("Test recipe")
            .servings(2)
            .readyInMinutes(20)
            .summary("This is only a test recipe")
            .extendedIngredients(new ArrayList<>())
            .build();

        String body = objectMapper.writeValueAsString(recipe);

        MvcResult mvcResult = this.mockMvc.perform(post(COOKBOOK_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
    }

    @Test
    @DisplayName("Negative test for creating a recipe with status 422")
    void createNonValidRecipeShouldReturnStatus422() throws Exception {
        RecipeSuggestionDto recipe = RecipeSuggestionDtoBuilder.builder()
            .summary("This is a non-valid test recipe")
            .extendedIngredients(new ArrayList<>())
            .build();

        String body = objectMapper.writeValueAsString(recipe);

        MvcResult mvcResult = this.mockMvc.perform(post(COOKBOOK_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

    @Test
    @DisplayName("Positive test for updating a recipe with status 200")
    void updateRecipeWithValidDataShouldReturnStatus200() throws Exception {
        RecipeSuggestionDto updatedRecipeDto = RecipeSuggestionDtoBuilder.builder()
            .id(1L)
            .title("Updated Test Recipe")
            .servings(4)
            .readyInMinutes(30)
            .summary("This is an updated test recipe")
            .extendedIngredients(new ArrayList<>())
            .build();

        String body = objectMapper.writeValueAsString(updatedRecipeDto);

        MvcResult mvcResult = this.mockMvc.perform(get(COOKBOOK_BASE_URI + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
    }

    @Test
    @DisplayName("Negative test for updating a recipe with status 404")
    void updateNonExistingRecipeShouldReturnStatus404() throws Exception {
        RecipeSuggestionDto updatedRecipeDto = RecipeSuggestionDtoBuilder.builder()
            .id(1000L)
            .title("Updated Test Recipe")
            .servings(4)
            .readyInMinutes(30)
            .summary("This is an updated test recipe")
            .extendedIngredients(new ArrayList<>())
            .build();

        String body = objectMapper.writeValueAsString(updatedRecipeDto);

        MvcResult mvcResult = this.mockMvc.perform(put(COOKBOOK_BASE_URI + "/1000")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    @DisplayName("Positive test for updating a recipe with status 200")
    void deleteExistingRecipeReturnStatus200() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(delete(COOKBOOK_BASE_URI + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
    }

    @Test
    @DisplayName("Negative test for updating a recipe with status 404")
    void deleteNonExistingRecipeReturnStatus404() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(delete(COOKBOOK_BASE_URI + "/1000")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    void getMissingIngredientsReturnStatus200() throws Exception {

        List<RecipeIngredientDto> ingredients = new ArrayList<>();

        Unit g = unitRepository.findByName("g").orElseThrow();
        Unit ml = unitRepository.findByName("ml").orElseThrow();

        RecipeIngredient ingredient1_1 = new RecipeIngredient();
        ingredient1_1.setName("Ground Beef");
        ingredient1_1.setAmount(500);
        ingredient1_1.setUnit(g.getName());
        ingredient1_1.setUnitEnum(g);
        ingredients.add(ingredientMapper.entityToDto(ingredient1_1));

        RecipeIngredient ingredient1_2 = new RecipeIngredient();
        ingredient1_2.setName("Tomato Sauce");
        ingredient1_2.setAmount(400);
        ingredient1_2.setUnit(ml.getName());
        ingredient1_2.setUnitEnum(ml);
        ingredients.add(ingredientMapper.entityToDto(ingredient1_2));

        RecipeSuggestionDto recipe = RecipeSuggestionDtoBuilder.builder()
            .id(1L)
            .title("Spaghetti Bolognese")
            .servings(4)
            .readyInMinutes(30)
            .summary("Classic Italian pasta dish with savory meat sauce. Begin by browning 500g of ground beef in a pan. Add 400ml of tomato sauce and let it simmer, allowing the flavors to meld. Meanwhile, cook spaghetti until al dente. Serve the rich meat sauce over the perfectly cooked spaghetti.")
            .extendedIngredients(ingredients)
            .build();

        String body = objectMapper.writeValueAsString(recipe);

        MvcResult mvcResult = this.mockMvc.perform(get(COOKBOOK_BASE_URI + "/missing/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        RecipeSuggestionDto recipeWithMissing = objectMapper.readValue(response.getContentAsString(), RecipeSuggestionDto.class);

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType()),
            () -> assertThat(recipeWithMissing.title()).isEqualTo(recipe.title()),
            () -> assertThat(recipeWithMissing.summary()).isEqualTo(recipe.summary()),
            () -> assertThat(recipeWithMissing.readyInMinutes()).isEqualTo(recipe.readyInMinutes()),
            () -> assertThat(recipeWithMissing.servings()).isEqualTo(recipe.servings()),
            () -> assertThat(recipe.missedIngredients()).isNull(),
            () -> assertThat(recipeWithMissing.missedIngredients()).isNotNull()
        );

    }

    @Test
    void getAllRecipesFromCookbookReturnsStatus200() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(COOKBOOK_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)

                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<RecipeSuggestionDto> recipes = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<RecipeSuggestionDto>>() {
        });

        assertThat(recipes.size()).isEqualTo(5);
    }

    @Test
    void getCookbookRecipeReturnStatus200() throws Exception {

        List<RecipeIngredientDto> ingredients = new ArrayList<>();

        Unit g = unitRepository.findByName("g").orElseThrow();
        Unit ml = unitRepository.findByName("ml").orElseThrow();

        RecipeIngredient ingredient1_1 = new RecipeIngredient();
        ingredient1_1.setName("Ground Beef");
        ingredient1_1.setAmount(500);
        ingredient1_1.setUnit(g.getName());
        ingredient1_1.setUnitEnum(g);
        ingredients.add(ingredientMapper.entityToDto(ingredient1_1));

        RecipeIngredient ingredient1_2 = new RecipeIngredient();
        ingredient1_2.setName("Tomato Sauce");
        ingredient1_2.setAmount(400);
        ingredient1_2.setUnit(ml.getName());
        ingredient1_2.setUnitEnum(ml);
        ingredients.add(ingredientMapper.entityToDto(ingredient1_2));

        RecipeSuggestionDto mockedRecipe = RecipeSuggestionDtoBuilder.builder()
            .id(1L)
            .title("Spaghetti Bolognese")
            .servings(4)
            .readyInMinutes(30)
            .summary("Classic Italian pasta dish with savory meat sauce. Begin by browning 500g of ground beef in a pan. Add 400ml of tomato sauce and let it simmer, allowing the flavors to meld. Meanwhile, cook spaghetti until al dente. Serve the rich meat sauce over the perfectly cooked spaghetti.")
            .extendedIngredients(ingredients)
            .build();

        String body = objectMapper.writeValueAsString(mockedRecipe);

        MvcResult mvcResult = this.mockMvc.perform(get(COOKBOOK_BASE_URI + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        RecipeSuggestionDto recipe = objectMapper.readValue(response.getContentAsString(), RecipeSuggestionDto.class);

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType()),
            () -> assertThat(mockedRecipe.title()).isEqualTo(recipe.title()),
            () -> assertThat(mockedRecipe.summary()).isEqualTo(recipe.summary()),
            () -> assertThat(mockedRecipe.readyInMinutes()).isEqualTo(recipe.readyInMinutes()),
            () -> assertThat(mockedRecipe.servings()).isEqualTo(recipe.servings())
        );

    }

    @Test
    void unMatchIngredientShouldReturnStatus200() throws Exception {


        // given
        String ingredientName = recipeIngredientRepository.findAll().get(0).getName();

        // when
        MvcResult mvcResult = this.mockMvc.perform(put(BASE_URI + "/unmatchitems")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ingredientName)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andReturn();

        // then
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
    }


    private void mockAPIResponse() {
        List<RecipeDto> mockedRecipesDtos = getRecipeDtos();
        RecipeSuggestionDto mockedRecipeSuggestionDto = getRecipeSuggestionDtoWithoutUnits();


        ParameterizedTypeReference<List<RecipeDto>> ref = new ParameterizedTypeReference<List<RecipeDto>>() {
        };
        ParameterizedTypeReference<RecipeSuggestionDto> ref2 = new ParameterizedTypeReference<RecipeSuggestionDto>() {
        };

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(ref)))
            .thenReturn(ResponseEntity.ok(mockedRecipesDtos));
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(ref2)))
            .thenReturn(ResponseEntity.ok(mockedRecipeSuggestionDto));
        ;

    }

    private List<RecipeDto> getRecipeDtos() {
        RecipeDto mockedRecipe1 = RecipeDtoBuilder.builder()
            .id(1L)
            .title("Pasta Carbonara")
            .description("Classic Italian pasta dish with eggs, cheese, pancetta, and black pepper.")
            .image("image1.jpg")
            .missedIngredients(List.of(
                RecipeIngredientDtoBuilder.builder()
                    .id(1L)
                    .name("Ingredient 1")
                    .unit("unit1")
                    .unitEnum(null)
                    .amount(100.0)
                    .matched(true)
                    .autoMatched(false)
                    .realName("Real Ingredient 1")
                    .matchedItem(null)
                    .build()
            ))
            .build();
        List<RecipeDto> toReturn = new LinkedList<>();
        toReturn.add(mockedRecipe1);
        return toReturn;
    }

    private RecipeSuggestionDto getRecipeSuggestionDtoWithoutUnits() {
        RecipeSuggestionDto recipeDto2 = RecipeSuggestionDtoBuilder.builder()
            .id(123123L)
            .title("Pasta Carbonara")
            .servings(4)
            .readyInMinutes(25)
            .summary("Classic Italian pasta dish with eggs, cheese, pancetta, and black pepper.")
            .extendedIngredients(List.of(
                RecipeIngredientDtoBuilder.builder()
                    .id(4L)
                    .name("Spaghetti")
                    .unit("g")
                    .amount(400.0)
                    .matched(true)
                    .autoMatched(false)
                    .realName("Spaghetti")
                    .matchedItem(null)
                    .build(),
                RecipeIngredientDtoBuilder.builder()
                    .id(5L)
                    .name("Pancetta")
                    .unit("g")
                    .amount(150.0)
                    .matched(true)
                    .autoMatched(false)
                    .realName("Pancetta")
                    .matchedItem(null)
                    .build(),
                RecipeIngredientDtoBuilder.builder()
                    .id(6L)
                    .name("Eggs")
                    .unit("pcs")
                    .amount(3.0)
                    .matched(true)
                    .autoMatched(false)
                    .realName("Eggs")
                    .matchedItem(null)
                    .build(),
                RecipeIngredientDtoBuilder.builder()
                    .id(7L)
                    .name("Parmesan cheese")
                    .unit("g")
                    .amount(100.0)
                    .matched(true)
                    .autoMatched(false)
                    .realName("Parmesan cheese")
                    .matchedItem(null)
                    .build()
            ))
            .build();


        return recipeDto2;
    }

}
