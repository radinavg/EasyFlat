package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemSearchDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemOrderType;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.SharedFlatService;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class StorageEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityProperties securityProperties;


    @Autowired
    private TestDataGenerator testDataGenerator;

    @Autowired
    private SharedFlatService sharedFlatService;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private AuthService authService;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    private final String BASE_URI = "/api/v1/storage";
    private final String ITEM_ENDPOINT_URI = BASE_URI + "/items";

    private ApplicationUser applicationUser;

    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        testDataGenerator.cleanUp();

        applicationUser = userRepository.findById(1L).orElseThrow();
        when(authService.getUserFromToken()).thenReturn(applicationUser);
    }

    @Test
    @DisplayName("Given storageId when getStorageById then storage returned")
    public void givenStorageIdAndSearchParametersWhenGetItemsThenItemsRetrieved() throws Exception {
        // Given


        // when
        ItemSearchDto itemSearchDto = new ItemSearchDto(false, null, null, null, null);

        MvcResult mvcResult = this.mockMvc.perform(get(ITEM_ENDPOINT_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .param("alwaysInStock", String.valueOf(itemSearchDto.alwaysInStock()))
                .param("productName", itemSearchDto.productName()))
            .andDo(print())
            .andReturn();

        // Then
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
            () -> assertNotNull(mvcResult.getResponse())
        );
    }

    @Test
    @DisplayName("Given storageId when getStorageById then storage returned")
    public void givenStorageIdAndOrderTypeNameWhenGetItemsThenItemsRetrievedInCorrectOrder() throws Exception {
        // Given
        ItemSearchDto itemSearchDto = ItemSearchDtoBuilder.builder()
            .alwaysInStock(false)
            .orderType(ItemOrderType.GENERAL_NAME)
            .build();

        MvcResult mvcResult = this.mockMvc.perform(get(ITEM_ENDPOINT_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .param("alwaysInStock", String.valueOf(itemSearchDto.alwaysInStock()))
                .param("orderType", itemSearchDto.orderType().toString())
                .param("desc", false + ""))
            .andDo(print())
            .andReturn();

        // Assertions
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
            () -> assertNotNull(mvcResult.getResponse()),
            () -> assertThat(mvcResult.getResponse()
                .getContentAsString()
                .substring(1, mvcResult.getResponse().getContentAsString().length() - 1)
                .toLowerCase()
                .split("\\{\"generalName\"")
            ).isSorted()
        );
    }

    @Test
    @DisplayName("Get all storages for the flat of the current user")
    public void givenNothingWhenGetAllThenStorageRetrieved() throws Exception {
        // when
        MvcResult mvcResult = this.mockMvc.perform(get(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, DigitalStorageDto.class);
        List<DigitalStorageDto> storageList = objectMapper.readValue(response.getContentAsString(), type);

        // then
        assertAll(
            () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE),
            () -> assertThat(storageList.size()).isEqualTo(1),
            () -> assertThat(storageList.get(0)).isNotNull(),
            () -> assertThat(storageList.get(0).title()).isEqualTo("Storage 1"),
            () -> assertThat(storageList.get(0).storageId()).isEqualTo(applicationUser.getSharedFlat().getDigitalStorage().getStorageId()),
            () -> assertThat(storageList.get(0).sharedFlat().getId()).isEqualTo(applicationUser.getSharedFlat().getId())
        );
    }

}