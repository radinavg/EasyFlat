package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChoreDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.JavaType;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserEndpointTest implements TestData {

    @Autowired
    private LoginEndpoint loginEndpoint;

    @Autowired
    private RegisterEndpoint registerEndpoint;

    private ApplicationUser applicationUser;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private TestDataGenerator testDataGenerator;

    @MockBean
    private AuthService authService;

    @Autowired
    private SecurityProperties securityProperties;

    private final String LOGIN_BASE_URI = "/api/v1/authentication";
    private final String REGISTER_BASE_URI = "/api/v1/register";

    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        testDataGenerator.cleanUp();
        applicationUser = userRepository.findById(1L).orElseThrow();
        when(authService.getUserFromToken()).thenReturn(applicationUser);
    }

    @Test
    @Disabled
    @DisplayName("Positive test for registering a valid user")
    public void registerAValidUserShouldResultStatus200() throws Exception {
        UserDetailDto userDetailDto = new UserDetailDto();
        userDetailDto.setFirstName("Max");
        userDetailDto.setLastName("Doe");
        userDetailDto.setEmail("max@example.com");
        userDetailDto.setPassword("P@ssw0rd");

        mockMvc.perform(post(REGISTER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDetailDto)))
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    @Disabled
    @DisplayName("Negative test for registering an already existing user")
    public void registerUserWithDuplicateDataShouldResultStatus403() throws Exception {
        UserDetailDto existingUser = new UserDetailDto();
        existingUser.setFirstName("Existing");
        existingUser.setLastName("User");
        existingUser.setEmail("existing@email.com");
        existingUser.setPassword("password");

        registerEndpoint.register(existingUser);

        mockMvc.perform(post(REGISTER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(existingUser)))
            .andExpect(status().isConflict());
    }


    @Test
    @DisplayName("Positive test for login as an existing user")
    public void loginAsAnExistingUserShouldResultStatus200() throws Exception {
        UserDetailDto user = new UserDetailDto();
        user.setFirstName("Annie");
        user.setLastName("Doe");
        user.setEmail("annie@example.com");
        user.setPassword("password");
        registerEndpoint.register(user);

        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setEmail("annie@example.com");
        userLoginDto.setPassword("password");

        mockMvc.perform(post(LOGIN_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLoginDto)))
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    @DisplayName("Negative test for logging in as a non-existing user")

    public void loginWithInvalidCredentialsShouldResultStatus404() throws Exception {
        UserLoginDto invalidUserLoginDto = new UserLoginDto();
        invalidUserLoginDto.setEmail("invalid@example.com");
        invalidUserLoginDto.setPassword("invalidPassword");

        mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUserLoginDto)))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Positive test for deleting an existing user")
    @Disabled
    public void deleteAnExistingUserShouldResultStatus200() throws Exception {
        UserDetailDto user = new UserDetailDto();
        user.setFirstName("DeleteUser");
        user.setLastName("Doe");
        user.setEmail("delete@example.com");
        user.setPassword("password");
        registerEndpoint.register(user);

        ApplicationUser userByEmail = userRepository.findUserByEmail(user.getEmail());

        mockMvc.perform(delete(LOGIN_BASE_URI + "/" + userByEmail.getId()))

            .andExpect(status().isOk());
    }

}
