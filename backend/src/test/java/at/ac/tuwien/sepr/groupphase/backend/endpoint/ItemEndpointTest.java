package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.ItemService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_USER;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.invalidItemDto;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.invalidItemId;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.invalidUpdatedItemDto;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.itemDtoWithInvalidDigitalStorage;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.validAlwaysInStockItem;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.validItemDto;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.validUpdatedItemDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
class ItemEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private TestDataGenerator testDataGenerator;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private AuthService authService;

    @Autowired
    private ItemService itemService;

    private final String BASE_URI = "/api/v1/item";
    private ApplicationUser applicationUser;

    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        testDataGenerator.cleanUp();

        applicationUser = userRepository.findById(1L).orElseThrow();
        when(authService.getUserFromToken()).thenReturn(applicationUser);
    }

    @Test
    @DisplayName("Given valid item when create then item is created")
    public void givenItemWhenCreateThenItemIsCreated() throws Exception {
        // given

        String body = objectMapper.writeValueAsString(validItemDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        // then
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        ItemDto item = objectMapper.readValue(response.getContentAsString(),
            ItemDto.class);

        Assertions.assertThat(item)
            .extracting(
                ItemDto::ean,
                ItemDto::generalName,
                ItemDto::productName,
                ItemDto::brand,
                ItemDto::quantityCurrent,
                ItemDto::quantityTotal,
                ItemDto::unit,
                ItemDto::expireDate,
                ItemDto::description,
                ItemDto::priceInCent,
                ItemDto::digitalStorage,
                ItemDto::boughtAt
            )
            .containsExactly(
                validItemDto.ean(),
                validItemDto.generalName(),
                validItemDto.productName(),
                validItemDto.brand(),
                validItemDto.quantityCurrent(),
                validItemDto.quantityTotal(),
                validItemDto.unit(),
                validItemDto.expireDate(),
                validItemDto.description(),
                validItemDto.priceInCent(),
                validItemDto.digitalStorage(),
                validItemDto.boughtAt()
            );
        assertThat(
            item.ingredients().stream()
                .map(IngredientDto::name)
                .toList()
        ).isEqualTo(
            validItemDto.ingredients().stream()
                .map(IngredientDto::name)
                .toList()
        );
    }

    @Test
    @DisplayName("Given item when create then item is created with alternative names")
    public void givenInvalidStorageWhenCreateThenValidationException() throws Exception {
        // given


        String body = objectMapper.writeValueAsString(invalidItemDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        // then
        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus()),
            () -> {
                String content = response.getContentAsString();
                String[] errors = content.split(",");
                assertEquals(6, errors.length);
            }
        );
    }

    @Test
    @DisplayName("Given item when create then item is created with alternative names")
    public void givenInvalidStorageWhenCreateThenAuthenticationException() throws Exception {
        // given
        String body = objectMapper.writeValueAsString(itemDtoWithInvalidDigitalStorage);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        // then
        assertAll(
            () -> assertEquals(HttpStatus.CONFLICT.value(), response.getStatus()),
            () -> {
                String content = response.getContentAsString();
                assertThat(content).contains("not");
            }
        );
    }

    @Test
    @DisplayName("Does findAllDelivers all items with limit")
    public void doesFindAllDeliversAllItemsWithLimit() throws Exception {
        // given
        // when
        MvcResult mvcResult = this.mockMvc.perform(get(BASE_URI)
                .param("limit", "5")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        // then
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> {
                String content = response.getContentAsString();
                List<ItemDto> items = objectMapper.readValue(content, new TypeReference<>() {
                });
                assertThat(items.size()).isEqualTo(5);
            }
        );
    }

    @Test
    @DisplayName("Given invalid item name, then findByName returns empty list")
    void givenInvalidItemNameThanFindByNameReturnsEmptyList() throws Exception {
        String itemName = "InvalidProductName";
        String unitName = "pcs";

        // when
        MvcResult mvcResult = this.mockMvc.perform(get(BASE_URI + "/name/InvalidProductName")
                .param("unitName", unitName)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        // then
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> {
                String content = response.getContentAsString();
                List<ItemDto> foundItems = objectMapper.readValue(content, new TypeReference<>() {
                });
                assertThat(foundItems.size()).isEqualTo(0);
            }
        );
    }


    @Test
    @DisplayName("Given valid item name, then findByName returns non-empty list")
    void givenValidItemNameThanFindByNameReturnsNonEmptyList() throws Exception {

        DigitalStorageItem validItem = itemService.findById(1L);

        // when
        MvcResult mvcResult = this.mockMvc.perform(get(BASE_URI + "/name/" + validItem.getItemCache().getProductName())
                .param("unitName", validItem.getItemCache().getUnit().getName())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        // then
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> {
                String content = response.getContentAsString();
                List<ItemDto> foundItems = objectMapper.readValue(content, new TypeReference<>() {
                });
                assertAll(
                    () -> assertThat(foundItems.size()).isNotEqualTo(0),
                    () -> assertThat(foundItems.get(0).productName()).isEqualTo(validItem.getItemCache().getProductName())
                );

            }

        );
    }

    @Test
    @DisplayName("Does findById delivers item")
    public void doesFindByIdDeliversItem() throws Exception {
        // given
        DigitalStorageItem item = itemService.create(validItemDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(get(BASE_URI + "/" + item.getItemId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String content = response.getContentAsString();
        ItemDto item2 = objectMapper.readValue(content, ItemDto.class);
        // then
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertThat(item2.itemId()).isEqualTo(item.getItemId()),
            () -> assertThat(item2).extracting(
                ItemDto::ean,
                ItemDto::generalName,
                ItemDto::productName,
                ItemDto::brand,
                ItemDto::quantityCurrent,
                ItemDto::quantityTotal,
                ItemDto::unit,
                ItemDto::expireDate,
                ItemDto::description,
                ItemDto::priceInCent,
                (i) -> i.digitalStorage().storageId(),
                ItemDto::boughtAt
            ).containsExactly(
                validItemDto.ean(),
                validItemDto.generalName(),
                validItemDto.productName(),
                validItemDto.brand(),
                validItemDto.quantityCurrent(),
                validItemDto.quantityTotal(),
                validItemDto.unit(),
                validItemDto.expireDate(),
                validItemDto.description(),
                validItemDto.priceInCent(),
                validItemDto.digitalStorage().storageId(),
                validItemDto.boughtAt()
            ),
            () -> assertThat(
                item2.ingredients().stream()
                    .map(IngredientDto::name)
                    .toList()
            ).isEqualTo(
                validItemDto.ingredients().stream()
                    .map(IngredientDto::name)
                    .toList()
            )
        );
    }

    @Test
    @DisplayName("Does findByField boughtAt delivers relevant items")
    public void doesFindByFieldDeliversRelevantItems() throws Exception {
        // given
        itemService.create(validItemDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(get(BASE_URI + "/search")
                .param("boughtAt", "Pagro")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String content = response.getContentAsString();
        List<ItemDto> items = objectMapper.readValue(content, new TypeReference<>() {
        });
        // then
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertThat(items.size()).isEqualTo(2),
            () -> assertThat(items.get(0)).extracting(
                ItemDto::ean,
                ItemDto::generalName,
                ItemDto::productName,
                ItemDto::brand,
                ItemDto::quantityCurrent,
                ItemDto::quantityTotal,
                ItemDto::unit,
                ItemDto::expireDate,
                ItemDto::description,
                ItemDto::priceInCent,
                (i) -> i.digitalStorage().storageId(),
                ItemDto::boughtAt
            ).containsExactly(
                validAlwaysInStockItem.ean(),
                validAlwaysInStockItem.generalName(),
                validAlwaysInStockItem.productName(),
                validAlwaysInStockItem.brand(),
                validAlwaysInStockItem.quantityCurrent(),
                validAlwaysInStockItem.quantityTotal(),
                validAlwaysInStockItem.unit(),
                validAlwaysInStockItem.expireDate(),
                validAlwaysInStockItem.description(),
                validAlwaysInStockItem.priceInCent(),
                validAlwaysInStockItem.digitalStorage().storageId(),
                validAlwaysInStockItem.boughtAt()
            ),
            () -> assertThat(
                items.get(0).ingredients().stream()
                    .map(IngredientDto::name)
                    .toList()
            ).isEqualTo(
                validAlwaysInStockItem.ingredients().stream()
                    .map(IngredientDto::name)
                    .toList()
            )
        );
    }

    @Test
    @DisplayName("Does findByField brand delivers relevant items")
    public void doesFindByFieldDeliversRelevantItemsBrand() throws Exception {
        // given
        itemService.create(validItemDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(get(BASE_URI + "/search")
                .param("brand", "Kraft")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String content = response.getContentAsString();
        List<ItemDto> items = objectMapper.readValue(content, new TypeReference<>() {
        });
        // then
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertThat(items.size()).isEqualTo(2),
            () -> assertThat(items.get(0)).extracting(
                ItemDto::ean,
                ItemDto::generalName,
                ItemDto::productName,
                ItemDto::brand,
                ItemDto::quantityCurrent,
                ItemDto::quantityTotal,
                ItemDto::unit,
                ItemDto::expireDate,
                ItemDto::description,
                ItemDto::priceInCent,
                (i) -> i.digitalStorage().storageId(),
                ItemDto::boughtAt
            ).containsExactly(
                validItemDto.ean(),
                validItemDto.generalName(),
                validItemDto.productName(),
                validItemDto.brand(),
                validItemDto.quantityCurrent(),
                validItemDto.quantityTotal(),
                validItemDto.unit(),
                validItemDto.expireDate(),
                validItemDto.description(),
                validItemDto.priceInCent(),
                validItemDto.digitalStorage().storageId(),
                validItemDto.boughtAt()
            ),
            () -> assertThat(
                items.get(0).ingredients().stream()
                    .map(IngredientDto::name)
                    .toList()
            ).isEqualTo(
                validItemDto.ingredients().stream()
                    .map(IngredientDto::name)
                    .toList()
            )
        );
    }

    @Test
    @DisplayName("Does findByField generalName delivers relevant items")
    public void doesFindByFieldDeliversRelevantItemsGeneralName() throws Exception {
        // given
        itemService.create(validItemDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(get(BASE_URI + "/search")
                .param("generalName", "spreads")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String content = response.getContentAsString();
        List<ItemDto> items = objectMapper.readValue(content, new TypeReference<>() {
        });
        // then
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertThat(items.size()).isEqualTo(2),
            () -> assertThat(items.get(0)).extracting(
                ItemDto::ean,
                ItemDto::generalName,
                ItemDto::productName,
                ItemDto::brand,
                ItemDto::quantityCurrent,
                ItemDto::quantityTotal,
                ItemDto::unit,
                ItemDto::expireDate,
                ItemDto::description,
                ItemDto::priceInCent,
                (i) -> i.digitalStorage().storageId(),
                ItemDto::boughtAt
            ).containsExactly(
                validItemDto.ean(),
                validItemDto.generalName(),
                validItemDto.productName(),
                validItemDto.brand(),
                validItemDto.quantityCurrent(),
                validItemDto.quantityTotal(),
                validItemDto.unit(),
                validItemDto.expireDate(),
                validItemDto.description(),
                validItemDto.priceInCent(),
                validItemDto.digitalStorage().storageId(),
                validItemDto.boughtAt()
            ),
            () -> assertThat(
                items.get(0).ingredients().stream()
                    .map(IngredientDto::name)
                    .toList()
            ).isEqualTo(
                validItemDto.ingredients().stream()
                    .map(IngredientDto::name)
                    .toList()
            )
        );
    }

    @Test
    @DisplayName("Does findByGeneralName delivers correct results")
    public void doesFindByGeneralNameDeliversCorrectResults() throws Exception {
        // given
        itemService.create(validItemDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(get(BASE_URI + "/general-name/spreads")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String content = response.getContentAsString();
        List<ItemDto> items = objectMapper.readValue(content, new TypeReference<>() {
        });
        // then
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertThat(items.size()).isEqualTo(2),
            () -> assertThat(items.get(0)).extracting(
                ItemDto::ean,
                ItemDto::generalName,
                ItemDto::productName,
                ItemDto::brand,
                ItemDto::quantityCurrent,
                ItemDto::quantityTotal,
                ItemDto::unit,
                ItemDto::expireDate,
                ItemDto::description,
                ItemDto::priceInCent,
                (i) -> i.digitalStorage().storageId(),
                ItemDto::boughtAt
            ).containsExactly(
                validItemDto.ean(),
                validItemDto.generalName(),
                validItemDto.productName(),
                validItemDto.brand(),
                validItemDto.quantityCurrent(),
                validItemDto.quantityTotal(),
                validItemDto.unit(),
                validItemDto.expireDate(),
                validItemDto.description(),
                validItemDto.priceInCent(),
                validItemDto.digitalStorage().storageId(),
                validItemDto.boughtAt()
            ),
            () -> assertThat(
                items.get(0).ingredients().stream()
                    .map(IngredientDto::name)
                    .toList()
            ).isEqualTo(
                validItemDto.ingredients().stream()
                    .map(IngredientDto::name)
                    .toList()
            )
        );
    }

    @Test
    @DisplayName("Update item with valid values")
    void updateItemWithValidValuesUpdatesItem() throws Exception {
        // given
        DigitalStorageItem createdItem = itemService.create(validItemDto);

        // when
        String updateItemBody = objectMapper.writeValueAsString(validUpdatedItemDto);

        MvcResult mvcResult = mockMvc.perform(put(BASE_URI + "/" + createdItem.getItemId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateItemBody)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse updateResponse = mvcResult.getResponse();
        ItemDto actualItemDto = objectMapper.readValue(updateResponse.getContentAsString(), ItemDto.class);

        // then
        assertAll(
            () -> assertThat(updateResponse.getStatus()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(updateResponse.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE),
            () -> assertThat(actualItemDto).extracting(
                ItemDto::ean,
                ItemDto::generalName,
                ItemDto::productName,
                ItemDto::brand,
                ItemDto::quantityCurrent,
                ItemDto::quantityTotal,
                ItemDto::unit,
                ItemDto::expireDate,
                ItemDto::description,
                ItemDto::priceInCent,
                (i) -> i.digitalStorage().storageId(),
                ItemDto::boughtAt
            ).containsExactly(
                validUpdatedItemDto.ean(),
                validUpdatedItemDto.generalName(),
                validUpdatedItemDto.productName(),
                validUpdatedItemDto.brand(),
                validUpdatedItemDto.quantityCurrent(),
                validUpdatedItemDto.quantityTotal(),
                validUpdatedItemDto.unit(),
                validUpdatedItemDto.expireDate(),
                validUpdatedItemDto.description(),
                validUpdatedItemDto.priceInCent(),
                validUpdatedItemDto.digitalStorage().storageId(),
                validUpdatedItemDto.boughtAt()
            ),
            () -> assertThat(
                actualItemDto.ingredients().stream()
                    .map(IngredientDto::name)
                    .toList()
            ).isEqualTo(
                validUpdatedItemDto.ingredients().stream()
                    .map(IngredientDto::name)
                    .toList()
            )
        );
    }

    @Test
    @DisplayName("Update item with invalid values")
    void updateWithInvalidValuesThrowsValidationException() throws Exception {
        // given
        DigitalStorageItem createdItem = itemService.create(validItemDto);

        // when
        String updateItemBody = objectMapper.writeValueAsString(invalidUpdatedItemDto);

        MvcResult mvcResult = mockMvc.perform(put(BASE_URI + "/" + createdItem.getItemId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateItemBody)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse updateResponse = mvcResult.getResponse();

        // then
        assertAll(
            () -> assertThat(updateResponse.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value()),
            () -> {
                String content = updateResponse.getContentAsString();
                String[] errors = content.split(",");
                assertEquals(2, errors.length);
            }
        );
    }

    @Test
    @DisplayName("Deleting an existing item should be possible")
    void givenItemWhenDeleteItemThenItemIsDeleted() throws Exception {
        // given
        DigitalStorageItem createdItem = itemService.create(validItemDto);

        // when
        MvcResult mvcResult = mockMvc.perform(delete(BASE_URI + "/" + createdItem.getItemId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse deleteResponse = mvcResult.getResponse();

        // then
        assertAll(
            () -> assertThat(deleteResponse.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value()),
            () -> assertThrows(NotFoundException.class, () -> itemService.findById(createdItem.getItemId()))
        );
    }

    @Test
    @DisplayName("Deleting a non existent item should not be possible")
    void givenInvalidIdWhenDeleteThrowsNotFoundException() throws Exception {
        // when
        MvcResult mvcResult = mockMvc.perform(delete(BASE_URI + "/" + invalidItemId)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse deleteResponse = mvcResult.getResponse();

        // then
        assertAll(
            () -> assertThat(deleteResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value())
        );
    }
}