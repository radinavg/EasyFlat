package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.datagenerator.CleanDatabase;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.SharedFlatRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import com.github.javafaker.App;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("unitTest")
public class SharedFlatServiceTest {
    @Autowired
    private CleanDatabase cleanDatabase;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private AuthService authService;
    @Autowired
    private SharedFlatService sharedFlatService;
    @Autowired
    private SharedFlatRepository sharedFlatRepository;
    @Autowired
    private UserRepository userRepository;

    private ApplicationUser testUser = new ApplicationUser(1L, "FirstName", "LastName", "user@email.com", "password", Boolean.FALSE, null);


    @BeforeEach
    public void cleanUp() {
        cleanDatabase.truncateAllTablesAndRestartIds();
        testUser.setPoints(0);
        testUser.setSharedFlat(new SharedFlat().setId(1L));
        when(authService.getUserFromToken()).thenReturn(testUser);
    }
    @Test
    void createFlatShouldSucceed() throws ValidationException, ConflictException {
        WgDetailDto newSharedFlat = new WgDetailDto();
        newSharedFlat.setName("New Shared Flat");
        newSharedFlat.setPassword("12341234");
        WgDetailDto result = sharedFlatService.create(newSharedFlat);

        assertAll(
            () -> assertNotNull(result),
            () -> assertTrue(sharedFlatRepository.findById(1L).isPresent()),
            () -> assertEquals(newSharedFlat.getName(), sharedFlatRepository.findById(1L).get().getName())
        );
    }

    @Test
    void createFlatShouldFailWithValidationException() {
        WgDetailDto newSharedFlat = new WgDetailDto();
        newSharedFlat.setName("New Shared Flat");

        assertAll(
            () -> assertThrows(ValidationException.class, () -> sharedFlatService.create(newSharedFlat))
        );
    }

    @Test
    void loginWgShouldSucceed() {
        when(passwordEncoder.matches(any(),any())).thenReturn(true);
        WgDetailDto newSharedFlat1 = new WgDetailDto();
        newSharedFlat1.setName("New Shared Flat");
        newSharedFlat1.setPassword("12341234");

        SharedFlat newSharedFlat = new SharedFlat();
        newSharedFlat.setName("New Shared Flat");
        newSharedFlat.setPassword("12341234");
        sharedFlatRepository.save(newSharedFlat);

        ApplicationUser admin = new ApplicationUser(2L, "FirstName1", "LastName1", "user1@email.com", "password", Boolean.TRUE, newSharedFlat);
        userRepository.save(admin);

        WgDetailDto result = sharedFlatService.loginWg(newSharedFlat1);

        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals(newSharedFlat.getName(),result.getName())
        );
    }

    @Test
    void deleteFlatWihtOneUserShouldSucceed() throws AuthorizationException {
        SharedFlat newSharedFlat = new SharedFlat();
        newSharedFlat.setName("New Shared Flat");
        newSharedFlat.setPassword("12341234");
        sharedFlatRepository.save(newSharedFlat);
        testUser.setAdmin(true);
        testUser.setSharedFlat(newSharedFlat);
        userRepository.save(testUser);

        sharedFlatService.delete(testUser.getId());

        assertAll(
            () -> assertEquals(0, sharedFlatRepository.findAll().size())
        );
    }

    @Test
    void deleteFlatWithMoreUsersShouldSucceed() throws AuthorizationException {
        SharedFlat newSharedFlat = new SharedFlat();
        newSharedFlat.setName("New Shared Flat");
        newSharedFlat.setPassword("12341234");
        sharedFlatRepository.save(newSharedFlat);
        testUser.setAdmin(true);
        testUser.setSharedFlat(newSharedFlat);
        userRepository.save(testUser);

        ApplicationUser user = new ApplicationUser(2L, "FirstName1", "LastName1", "user1@email.com", "password", Boolean.FALSE, newSharedFlat);
        userRepository.save(user);

        sharedFlatService.delete(testUser.getId());
        assertAll(
            () -> assertEquals(0, sharedFlatRepository.findAll().size())
        );
    }
}