package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChoreDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChoreSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.RepeatChoreRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ChoreMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Chore;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChoreRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UnitRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.ChoreService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ChoreEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    private final String BASE_URI = "/api/v1/chores";

    @Autowired
    private TestDataGenerator testDataGenerator;

    @Autowired
    private UserRepository userRepository;

    private ApplicationUser applicationUser;

    @MockBean
    private AuthService authService;

    @Autowired
    private ChoreService choreService;

    @Autowired
    private ChoreMapper choreMapper;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private ChoreRepository choreRepository;

    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        testDataGenerator.cleanUp();
        applicationUser = userRepository.findById(1L).orElseThrow();
        when(authService.getUserFromToken()).thenReturn(applicationUser);
    }

    @Test
    void testCreateChoreChoreEndpoint() throws Exception {
        ChoreDto choreDto = new ChoreDto(null, "Clean the trash bin", "Clean the dirty spots from the trash bin", LocalDate.now().plusDays(3), "5", null);

        String body = objectMapper.writeValueAsString(choreDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        ChoreDto responseChoreDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ChoreDto.class);
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
            () -> assertNotNull(responseChoreDto),
            () -> assertEquals("Clean the trash bin", responseChoreDto.name())
        );
    }

    @Test
    void testGetChoresChoresEndpoint() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, ChoreDto.class);
        List<ChoreDto> choreList = objectMapper.readValue(response.getContentAsString(), type);

        // then
        assertAll(
            () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE),
            () -> assertThat(choreList.size()).isEqualTo(choreRepository.findAllBySharedFlatId(1L).size()),
            () -> assertThat(choreList.get(0)).isNotNull(),
            () -> assertThat(choreList.get(0).name()).isEqualTo(choreRepository.findAllBySharedFlatId(1L).get(0).getName())
        );
    }

    @Test
    void testGetUnassignedChoresChoresEndpoint() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(BASE_URI + "/unassigned")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, ChoreDto.class);
        List<ChoreDto> choreList = objectMapper.readValue(response.getContentAsString(), type);
        assertAll(
            () -> assertEquals(choreRepository.findAllBySharedFlatId(1L).size(), choreList.size()),
            () -> assertEquals(choreRepository.findAllBySharedFlatId(1L).get(0).getName(), choreList.get(0).name())
        );
    }

    @Test
    void testAssignChoresChoresEndpoint() throws Exception {
        MvcResult mvcResult = mockMvc.perform(put(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, ChoreDto.class);
        List<ChoreDto> choreList = objectMapper.readValue(response.getContentAsString(), type);

        assertAll(
            () -> assertEquals(choreRepository.findAllBySharedFlatId(1L).size(), choreList.size()),
            () -> assertEquals(choreRepository.findAllBySharedFlatId(1L).get(0).getName(), choreList.get(0).name())
        );
    }

    @Test
    void testGetChoresByUserChoresEndpoint() throws Exception {
        List<Chore> chores = choreRepository.findAllBySharedFlatId(applicationUser.getSharedFlat().getId());
        chores.get(0).setUser(applicationUser);
        choreRepository.save(chores.get(0));

        MvcResult mvcResult = mockMvc.perform(get(BASE_URI + "/user")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, ChoreDto.class);
        List<ChoreDto> choreList = objectMapper.readValue(response.getContentAsString(), type);
        assertAll(
            () -> assertEquals(choreRepository.findAllByUser(applicationUser).size(), choreList.size())
        );
    }

    @Test
    void testDeleteChoresChoresEndpoint() throws Exception {
        List<Chore> chores = choreRepository.findAllBySharedFlatId(applicationUser.getSharedFlat().getId());
        chores.get(0).setUser(applicationUser);
        choreRepository.save(chores.get(0));

        MvcResult mvcResult = mockMvc.perform(delete(BASE_URI + "/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .param("choreIds", String.valueOf(chores.get(0).getId()))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, ChoreDto.class);
        List<ChoreDto> choreList = objectMapper.readValue(response.getContentAsString(), type);
        assertAll(
            () -> assertEquals(choreRepository.findAllByUser(applicationUser).size(), choreList.size() - 1)
        );
    }

    @Test
    void testGetUsersChoresEndpoint() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(BASE_URI + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, ChoreDto.class);
        List<ChoreDto> userList = objectMapper.readValue(response.getContentAsString(), type);
        assertAll(
            () -> assertEquals(userRepository.findAllBySharedFlat(applicationUser.getSharedFlat()).size(), userList.size())
        );
    }

    @Test
    void testUpdatePointsChoresEndpoint() throws Exception {
        UserDetailDto userDetailDto = new UserDetailDto();
        userDetailDto.setId(1L);
        userDetailDto.setPoints(8);
        MvcResult mvcResult = mockMvc.perform(patch(BASE_URI + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDetailDto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        UserDetailDto responseUserDetailDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDetailDto.class);
        assertEquals(userRepository.findApplicationUserById(applicationUser.getId()).getPoints(), responseUserDetailDto.getPoints());
    }

    @Test
    void testGenerateChoreListPdfChoresEndpoint() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(BASE_URI + "/pdf")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    void testRepeatChoreChoresEndpoint() throws Exception {
        RepeatChoreRequest repeatChoreRequest = new RepeatChoreRequest();
        repeatChoreRequest.setId(1L);
        repeatChoreRequest.setDate(new Date());

        MvcResult mvcResult = mockMvc.perform(patch(BASE_URI + "/repeat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(repeatChoreRequest))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        ChoreDto responseChoreDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ChoreDto.class);
        assertEquals("Cleaning the Bathroom", responseChoreDto.name());
    }


}
