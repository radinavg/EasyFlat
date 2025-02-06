package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.datagenerator.ChoreDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.CleanDatabase;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.SharedFlatDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChoreDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PreferenceDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Chore;
import at.ac.tuwien.sepr.groupphase.backend.entity.Preference;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChoreRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PreferenceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("unitTest")
public class PreferenceServiceTest {

    @Autowired
    private SharedFlatDataGenerator sharedFlatDataGenerator;

    @Autowired
    private PreferenceService preferenceService;

    @MockBean
    private AuthService authService;

    @Autowired
    private PreferenceRepository preferenceRepository;

    @Autowired
    private ChoreRepository choreRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CleanDatabase cleanDatabase;

    private ApplicationUser testUser = new ApplicationUser(1L, "FirstName", "LastName", "user@email.com", "password", Boolean.FALSE, null);

    private PreferenceDto preferenceDto;

    private ChoreDto firstDto;

    private ChoreDto secondDto;

    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        cleanDatabase.truncateAllTablesAndRestartIds();
        sharedFlatDataGenerator.generateSharedFlats();
        testUser.setPoints(0);
        testUser.setSharedFlat(new SharedFlat().setId(1L));
        userRepository.save(testUser);
        when(authService.getUserFromToken()).thenReturn(testUser);

        firstDto = new ChoreDto(
            1L,
            "First",
            "",
            LocalDate.of(2022, 8, 18),
            "5",
            null
        );


        secondDto = new ChoreDto(
            2L,
            "Second",
            "Description for Chore 2",
            LocalDate.of(2022, 8, 18),
            "5",
            null
        );


    }

    @Test
    void updateWithValidPreferenceDtoShouldSucceed() throws ValidationException, AuthenticationException {
        Chore first = new Chore();
        first.setName("First");
        first.setDescription("");
        first.setEndDate(LocalDate.of(2022, 8, 18));
        first.setPoints(5);
        choreRepository.save(first);
        Chore second = new Chore();
        second.setName("Second");
        second.setDescription("Description for Chore 2");
        second.setEndDate(LocalDate.of(2022, 8, 18));
        second.setPoints(5);
        choreRepository.save(second);

        preferenceDto = new PreferenceDto(
            null,
            firstDto,
            secondDto,
            null,
            null
        );

        PreferenceDto result = this.preferenceService.update(preferenceDto);

        // Assertions
        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals(firstDto.name(), result.first().name()),
            () -> assertEquals(secondDto.name(), result.second().name()),
            () -> assertEquals(secondDto.description(), result.second().description()),
            () -> assertEquals(firstDto.points(), result.first().points()),
            () -> assertEquals(firstDto.endDate(), result.first().endDate()),
            () -> assertNull( result.third()),
            () -> assertNull( result.fourth())
        );
    }


    @Test
    void getLastPreferenceShouldSucceed() throws ValidationException, AuthenticationException {
        Chore first = new Chore();
        first.setName("First");
        first.setDescription("");
        first.setEndDate(LocalDate.of(2022, 8, 18));
        first.setPoints(5);
        choreRepository.save(first);
        Chore second = new Chore();
        second.setName("Second");
        second.setDescription("Description for Chore 2");
        second.setEndDate(LocalDate.of(2022, 8, 18));
        second.setPoints(5);
        choreRepository.save(second);

        preferenceDto = new PreferenceDto(
            null,
            firstDto,
            secondDto,
            null,
            null
        );

        PreferenceDto preference = this.preferenceService.update(preferenceDto);

        PreferenceDto result = preferenceService.getLastPreference();

        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals(preference.first().name(), result.first().name()),
            () -> assertEquals(preference.second().name(), result.second().name()),
            () -> assertEquals(preference.second().description(), result.second().description()),
            () -> assertEquals(preference.first().points(), result.first().points()),
            () -> assertEquals(preference.first().endDate(), result.first().endDate()),
            () -> assertNull( result.third()),
            () -> assertNull( result.fourth())
        );
    }
}
