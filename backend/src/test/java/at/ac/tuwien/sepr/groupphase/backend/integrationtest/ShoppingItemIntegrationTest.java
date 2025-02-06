package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemLabelDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ShoppingListMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.ShoppingListService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@Disabled
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ShoppingItemIntegrationTest implements TestData  {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShoppingItemRepository shoppingItemRepository;

    @Autowired
    private ShoppingListRepository shoppingListRepository;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private ShoppingListMapper shoppingListMapper;

    private final String baseUri = "/api/v1/shopping";
    private final ShoppingListDto shoppingListDto = new ShoppingListDto(1L, "Default", 0);
    private final ApplicationUser testUser = new ApplicationUser(null, "", "", "user@email.com", "password", Boolean.FALSE, null);

    private ShoppingListService shoppingListServiceMock;

    @Test
    @BeforeEach
    public void createShoppingItem_then200() throws ValidationException, AuthenticationException, ConflictException {
        ShoppingItemDto validShoppingItemDto = new ShoppingItemDto(
            null,
            "1234567890123",
            "pear",
            "pear1",
            "lidl",
            10.0,
            20.0,
            g,
            "Description",
            500L,
            true,
            5.0,
            "Store",
            null,
            new ArrayList<>(Collections.singleton(new ItemLabelDto(null, "fruit", "#ff0000"))), // Labels
            new ShoppingListDto(1L, "Default", 0));

    }

    private void createValidUserAndValidShoppingList() throws Exception {
        userRepository.save(testUser);
        MvcResult mvcResult = mockMvc.perform(post(baseUri + "/list-create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(shoppingListDto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user@email.com", USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
        );
    }

    @AfterEach
    public void deleteShoppingList() {
        shoppingListRepository.delete(shoppingListMapper.dtoToEntity(shoppingListDto));
    }

    @AfterEach
    public void deleteUser() {
        userRepository.delete(testUser);
    }


    @Test
    @BeforeEach
    public void testCreateValidShoppingItem_then201() throws Exception {
        createValidUserAndValidShoppingList();
        MvcResult mvcResult = mockMvc.perform(post(this.baseUri)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validShoppingItemDto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user@email.com", USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
        );
    }

    @Test
    @BeforeEach
    public void createInvalidShoppingItem_then409() throws Exception {
        createValidUserAndValidShoppingList();
        MvcResult mvcResult = mockMvc.perform(post(this.baseUri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidShoppingItemDto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user@email.com", USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
    }

    @Test
    @BeforeEach
    public void testUpdateValidShoppingItem_then200() throws Exception {
        createValidUserAndValidShoppingList();
        ShoppingItem saved = shoppingItemRepository.save(itemMapper.shoppingItemDtoToShoppingItemEntity(validShoppingItemDto, null));
        ShoppingItemDto updated = new ShoppingItemDto(
            saved.getItemId(),
            "1234567890123",
            "apple",
            "apple2", // updated product name
            "hoffer",
            10.0,
            20.0,
            kg,
            "Description",
            500L,
            true,
            5.0,
            "Store",
            null,
            new ArrayList<>(Collections.singleton(new ItemLabelDto(null, "fruit", "#ff0000"))), // Labels
            new ShoppingListDto(1L, "Default", 0));
        MvcResult mvcResult = mockMvc.perform(put(this.baseUri + "/" + updated.itemId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user@email.com", USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
        );
    }

    @Test
    @BeforeEach
    public void updateInvalidShoppingItem_then409() throws Exception {
        createValidUserAndValidShoppingList();
        ShoppingItem saved = shoppingItemRepository.save(itemMapper.shoppingItemDtoToShoppingItemEntity(validShoppingItemDto, null));
        ShoppingItemDto updated = new ShoppingItemDto(
            saved.getItemId(),
            "1234567890123",
            "apple",
            "apple1",
            "hoffer",
            10.0,
            20.0,
            g,
            "Description",
            500L,
            true,
            null, // minimumQuantity set to null, although alwaysInStock is true
            "Store",
            null,
            new ArrayList<>(Collections.singleton(new ItemLabelDto(null, "fruit", "#ff0000"))), // Labels
            new ShoppingListDto(1L, "Default", 0));
        MvcResult mvcResult = mockMvc.perform(put(this.baseUri + "/" + updated.itemId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user@email.com", USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
    }

}
