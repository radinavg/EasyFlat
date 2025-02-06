package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;


@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest implements TestData {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    private ApplicationUser applicationUser;

    @Autowired
    private TestDataGenerator testDataGenerator;

    @SpyBean
    private JwtTokenizer jwtTokenizer;

    @MockBean
    private AuthService authService;

    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        testDataGenerator.cleanUp();

        applicationUser = userRepository.findById(1L).orElseThrow();
        when(authService.getUserFromToken()).thenReturn(applicationUser);
    }

    @Test
    @DisplayName("Positive test for registering a valid user")
    public void registerValidUserAndCheckIfSuccessfullyRegistered() throws ValidationException, ConflictException {
        UserDetailDto userDetailDto = new UserDetailDto();
        userDetailDto.setFirstName("John");
        userDetailDto.setLastName("Doe");
        userDetailDto.setEmail("john.doe@example.com");
        userDetailDto.setPassword("password");

        String authToken = userService.register(userDetailDto);

        ApplicationUser registeredUser = userRepository.findUserByEmail("john.doe@example.com");
        assertNotNull(registeredUser);
        assertEquals("John", registeredUser.getFirstName());
        assertEquals("Doe", registeredUser.getLastName());
        assertEquals("john.doe@example.com", registeredUser.getEmail());
        assertEquals(passwordEncoder.matches("password", registeredUser.getPassword()), true);
        assertNotNull(authToken);
    }

    @Test
    @DisplayName("Negative test for registering a user with an existing email")
    public void registerUserWithExistingEmailShouldThrowException() throws ValidationException, ConflictException {
        UserDetailDto newUser = new UserDetailDto();
        newUser.setFirstName("Bob");
        newUser.setLastName("Johnson");
        newUser.setEmail(applicationUser.getEmail());
        newUser.setPassword("password");

        assertThrows(Exception.class, () -> userService.register(newUser));
    }

    @Test
    @DisplayName("Positive test for updating an existing user with valid data")
    public void updateExistingUserWithValidData() throws ValidationException, ConflictException {
        ApplicationUser fetchedUser1 = userRepository.findUserByEmail(applicationUser.getEmail());

        fetchedUser1.setFirstName("UpdatedFirstName");
        fetchedUser1.setLastName("UpdatedLastName");
        fetchedUser1.setPassword("newpassword123");
        userService.update(userMapper.entityToUserDetailDto(fetchedUser1));

        ApplicationUser fetchedUser = userRepository.findUserByEmail(applicationUser.getEmail());
        assertNotNull(fetchedUser);
        assertEquals("UpdatedFirstName", fetchedUser.getFirstName());
        assertEquals("UpdatedLastName", fetchedUser.getLastName());
        assertEquals(passwordEncoder.matches("newpassword123", fetchedUser.getPassword()), true);
    }

    @Test
    @DisplayName("Positive test for deleting an existing user")
    public void deleteExistingUserAndEnsureDeletion() {
        ApplicationUser user = userRepository.findUserByEmail(applicationUser.getEmail());

        userService.delete(applicationUser.getId());

        ApplicationUser deletedUserFromDB = userRepository.findUserByEmail(applicationUser.getEmail());
        assertNull(deletedUserFromDB, "Deleted user should not be found");
    }

    @Test
    void signOutWithManzObjectsShouldSucceed() throws AuthorizationException {
        ApplicationUser testUser = userRepository.findApplicationUserById(1L);
        userService.signOut(testUser.getSharedFlat().getName(), testUser.getId());

        assertAll(
            () -> assertNull(userRepository.findApplicationUserById(1L).getSharedFlat())
        );
    }

    @Test
    void signOutShouldFailWithAuthorizationException() {
        ApplicationUser testUser = userRepository.findApplicationUserById(1L);
        assertAll(
            () -> assertThrows(AuthorizationException.class, () -> userService.signOut(userRepository.findApplicationUserById(2L).getSharedFlat().getName(), testUser.getId()))
        );
    }

    @Test
    void getAllOtherUsersShouldSucceed() {
        ApplicationUser testUser = userRepository.findApplicationUserById(1L);
        List<UserDetailDto> users = userService.getAllOtherUsers(testUser.getId());

        assertAll(
            () -> assertEquals(userRepository.findAllBySharedFlat(testUser.getSharedFlat()).size() - 1, users.size())
        );
    }

    @Test
    void setAdminToTheFlatShouldSucceed() {
        ApplicationUser testUser = userRepository.findApplicationUserById(1L);
        this.userService.setAdminToTheFlat(testUser.getId());

        assertAll(
            () -> Assertions.assertTrue(testUser.getAdmin())
        );
    }

    @Test
    @DisplayName("finding All flatmates")
    public void findFlatmates() {
        // given
        doReturn(applicationUser.getEmail()).when(jwtTokenizer).getEmailFromToken("jwt");

        // when
        List<ApplicationUser> flatmates = userService.findFlatmates("jwt");

        // then
        assertAll(
            () -> assertThat(flatmates.size()).isEqualTo(5),
            () -> assertThat(flatmates.stream().map(user -> user.getId())).contains(1L, 6L, 11L, 16L, 21L)
        );
    }

}
