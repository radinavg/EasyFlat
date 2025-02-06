package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventLabelDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventLabelDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SharedFlatMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class EventServiceTest {
    @Autowired
    private EventsService eventsService;

    @MockBean
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SharedFlatMapper sharedFlatMapper;
    private ApplicationUser applicationUser;

    @Autowired
    private TestDataGenerator testDataGenerator;


    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        testDataGenerator.cleanUp();

        applicationUser = userRepository.findById(1L).orElseThrow();
        when(authService.getUserFromToken()).thenReturn(applicationUser);
    }

    @Test
    @DisplayName("Given Valid EventDto When Create Then Return Created EventDto")
    void givenValidEventDtoWhenCreateThenReturnCreatedEventDto() throws ValidationException {
        // given
        SharedFlat sharedFlat = new SharedFlat().setId(1L);


        EventDto eventDto = EventDtoBuilder.builder()
            .id(1L)
            .title("Test Title")
            .description("Test Description")
            .date(LocalDate.now().plusDays(1))
            .startTime(LocalTime.of(16,0))
            .endTime(LocalTime.of(17,0))
            .labels(new ArrayList<>())
            .sharedFlat(sharedFlatMapper.entityToWgDetailDto(sharedFlat))
            .build();


        // when
        EventDto result = eventsService.create(eventDto);

        //then
        assertAll(
            () -> assertThat(result.title()).isEqualTo(eventDto.title()),
            () -> assertThat(result.description()).isEqualTo(eventDto.description()),
            () -> assertThat(result.date()).isEqualTo(eventDto.date()),
            () -> assertThat(result.sharedFlat().getId()).isEqualTo(eventDto.sharedFlat().getId())
        );


    }

    @Test
    @DisplayName("Given Invalid EventDto With Empty Title When Create Then Throw ValidationException")
    void givenUpdatedEventDtoWhenUpdateThenReturnUpdatedEventDto() throws AuthorizationException, ValidationException {

        //given
        EventDto updatedEventDto = EventDtoBuilder.builder()
            .id(1L)
            .title("Updated Title")
            .description("Updated Description")
            .date(LocalDate.now().plusDays(2))
            .startTime(LocalTime.of(16,0))
            .endTime(LocalTime.of(17,0))
            .labels(new ArrayList<>())
            .sharedFlat(sharedFlatMapper.entityToWgDetailDto(new SharedFlat().setId(1L)))
            .build();

        //when
        EventDto result = eventsService.update(updatedEventDto);

        //then
        assertAll(
            () -> assertThat(result.title()).isEqualTo(updatedEventDto.title()),
            () -> assertThat(result.description()).isEqualTo(updatedEventDto.description()),
            () -> assertThat(result.date()).isEqualTo(updatedEventDto.date()),
            () -> assertThat(result.sharedFlat().getId()).isEqualTo(updatedEventDto.sharedFlat().getId())
        );
    }

    @Test
    @DisplayName("Given Updated EventDto When Update Then Return Updated EventDto")
    void givenInvalidEventDtoWithEmptyTitleWhenCreateThenThrowValidationException() {

        EventDto invalidEventDto = EventDtoBuilder.builder()
            .title("")
            .description("")
            .date(LocalDate.now().plusDays(2))
            .startTime(LocalTime.of(16,0))
            .endTime(LocalTime.of(17,0))
            .labels(new ArrayList<>())
            .sharedFlat(sharedFlatMapper.entityToWgDetailDto(new SharedFlat().setId(1L)))
            .build();


        assertThrows(ValidationException.class, () -> eventsService.create(invalidEventDto));
    }

    @Test
    @DisplayName("Given Non-Existing EventId When Update Then Throw EntityNotFoundException")
    void givenNonExistingEventIdWhenUpdateThenThrowEntityNotFoundException() {
        EventDto nonExistingEventDto = EventDtoBuilder.builder()
            .id(100L) // Assume ID 100 doesn't exist
            .title("Updated Title")
            .description("Updated Description")
            .date(LocalDate.now().plusDays(2))
            .startTime(LocalTime.of(16,0))
            .endTime(LocalTime.of(17,0))
            .labels(new ArrayList<>())
            .sharedFlat(sharedFlatMapper.entityToWgDetailDto(new SharedFlat().setId(1L)))
            .build();

        assertThrows(EntityNotFoundException.class, () -> eventsService.update(nonExistingEventDto));
    }

    @Test
    @DisplayName("Throw AuthorizationException when unauthorized user attempts to update an event")
    void givenUnauthorizedUserWhenUpdateThenThrowAuthorizationException() {
        applicationUser = userRepository.findById(2L).orElseThrow();
        when(authService.getUserFromToken()).thenReturn(applicationUser);

        EventDto eventDto = EventDtoBuilder.builder()
            .id(1L)
            .title("Updated Title")
            .description("Updated Description")
            .date(LocalDate.now().plusDays(2))
            .startTime(LocalTime.of(16,0))
            .endTime(LocalTime.of(17,0))
            .labels(new ArrayList<>())
            .sharedFlat(sharedFlatMapper.entityToWgDetailDto(new SharedFlat().setId(1L)))
            .build();

        assertThrows(AuthorizationException.class, () -> eventsService.update(eventDto));
    }

    @Test
    @DisplayName("Given Valid EventId When Delete Then Return Deleted EventDto")
    void givenValidEventIdWhenDeleteThenReturnDeletedEventDto() throws AuthorizationException {
        // given
        Long eventIdToDelete = 1L;

        // when
        EventDto result = eventsService.delete(eventIdToDelete);

        // then
        assertAll(
            () -> assertThat(result.title()).isNotNull(),
            () -> assertThat(result.description()).isNotNull(),
            () -> assertThat(result.date()).isNotNull(),
            () -> assertThat(result.sharedFlat().getId()).isNotNull()
        );
    }

    @Test
    @DisplayName("Given Non-Existing Event ID When Delete Then Throw NotFoundException")
    void givenNonExistingEventIdWhenDeleteThenThrowNotFoundException() {
        Long nonExistingEventId = 100L;

        assertThrows(NotFoundException.class, () -> eventsService.delete(nonExistingEventId));
    }

    @Test
    @DisplayName("Given Unauthorized User When Delete Then Throw AuthorizationException")
    void givenUnauthorizedUserWhenDeleteThenThrowAuthorizationException() {

        applicationUser = userRepository.findById(2L).orElseThrow();
        when(authService.getUserFromToken()).thenReturn(applicationUser);

        Long eventIdToDelete = 1L;

        assertThrows(AuthorizationException.class, () -> eventsService.delete(eventIdToDelete));
    }

    @Test
    @DisplayName("Given Valid User When Find All Then Return List of EventDtos")
    void givenValidUserWhenFindAllThenReturnListOfEventDtos() {
        // given
        List<EventDto> result = eventsService.findAll();

        // then
        assertThat(result).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("Given Valid Event ID When Get Event With ID Then Return EventDto")
    void givenValidEventIdWhenGetEventWithIdThenReturnEventDto() throws AuthorizationException {
        // given
        Long eventIdToRetrieve = 1L;

        // when
        EventDto result = eventsService.getEventWithId(eventIdToRetrieve);

        // then
        assertAll(
            () -> assertThat(result.title()).isNotNull(),
            () -> assertThat(result.description()).isNotNull(),
            () -> assertThat(result.date()).isNotNull(),
            () -> assertThat(result.sharedFlat().getId()).isNotNull()
        );
    }

    @Test
    @DisplayName("Given Non-Existing Event ID When Get Event With ID Then Throw EntityNotFoundException")
    void givenNonExistingEventIdWhenGetEventWithIdThenThrowEntityNotFoundException() {

        Long nonExistingEventId = 100L;

        assertThrows(EntityNotFoundException.class, () -> eventsService.getEventWithId(nonExistingEventId));
    }

    @Test
    @DisplayName("Given Invalid EventDto With End Time Before Start Time When Create Then Throw ValidationException")
    void givenInvalidEventDtoWithEndTimeBeforeStartTimeWhenCreateThenThrowValidationException() {

        EventDto invalidEventDto = EventDtoBuilder.builder()
            .title("Title")
            .startTime(LocalTime.of(12, 0))
            .endTime(LocalTime.of(11, 0))
            .date(LocalDate.now().plusDays(1))
            .labels(new ArrayList<>())
            .sharedFlat(sharedFlatMapper.entityToWgDetailDto(new SharedFlat().setId(1L)))
            .build();

        assertThrows(ValidationException.class, () -> eventsService.create(invalidEventDto));
    }

    @Test
    @DisplayName("Positive test for exporting a valid event")
    void givenValidEventIdExportShouldSucceed() throws AuthorizationException {

       String exported = this.eventsService.exportEvent(1L);

       assertAll(
           () -> assertNotNull(exported),
           () -> assertTrue(exported.startsWith("BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:-//EasyFlat//\n")),
           () -> assertTrue(exported.contains("House Meeting")),
           () -> assertTrue(exported.contains("Discussing important matters regarding the shared living space.")),
           () -> assertTrue(exported.endsWith("END:VCALENDAR"))
       );
    }
    @Test
    @DisplayName("Positive Test for Exporting a Valid Event")
    void givenValidEventIdForAllDayEventExportShouldSucceed() throws AuthorizationException {

        String exported = this.eventsService.exportEvent(2L);

        assertAll(
            () -> assertNotNull(exported),
            () -> assertTrue(exported.startsWith("BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:-//EasyFlat//\n")),
            () -> assertTrue(exported.contains("Cleaning Day")),
            () -> assertTrue(exported.contains("A day dedicated to cleaning and maintaining the shared areas.")),
            () -> assertTrue(exported.endsWith("END:VCALENDAR"))
        );
    }

    @Test
    @DisplayName("Negative test for exporting a non-existing event")
    void givenInvalidEventIdExportShouldThrowEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> this.eventsService.exportEvent(1000L));
    }

    @Test
    @DisplayName("Positive test for exporting all events for the shared flat form the test data")
    void testExportAllWithGivenTestDataShouldSucceed() {

        String exported = this.eventsService.exportAll();

        assertAll(
            () -> assertNotNull(exported),
            () -> assertTrue(exported.startsWith("BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:-//EasyFlat//\n")),
            () -> assertTrue(exported.contains("House Meeting")),
            () -> assertTrue(exported.contains("Discussing important matters regarding the shared living space.")),
            () -> assertTrue(exported.contains("Cleaning Day")),
            () -> assertTrue(exported.contains("A day dedicated to cleaning and maintaining the shared areas.")),
            () -> assertTrue(exported.contains("Movie Night")),
            () -> assertTrue(exported.contains("Gathering for a cozy movie night in the common area.")),
            () -> assertTrue(exported.endsWith("END:VCALENDAR"))
        );
    }

    @Test
    @DisplayName("Positive test for updating an existing event with valid labels")
    void givenUpdatedEventWithLabelsShouldSucceed() throws AuthorizationException, ValidationException {

        EventLabelDto label1 = EventLabelDtoBuilder.builder()
            .labelName("label1")
            .build();
        EventLabelDto label2 = EventLabelDtoBuilder.builder()
            .labelName("label2")
            .build();
        List<EventLabelDto> labels = new ArrayList<>();
        labels.addAll(List.of(label1, label2));

        //given
        EventDto updatedEventDto = EventDtoBuilder.builder()
            .id(1L)
            .title("Updated Title")
            .description("Updated Description")
            .date(LocalDate.now().plusDays(2))
            .startTime(LocalTime.of(16,0))
            .endTime(LocalTime.of(17,0))
            .labels(labels)
            .sharedFlat(sharedFlatMapper.entityToWgDetailDto(new SharedFlat().setId(1L)))
            .build();

        //when
        EventDto result = eventsService.update(updatedEventDto);

        //then
        assertAll(
            () -> assertThat(result.title()).isEqualTo(updatedEventDto.title()),
            () -> assertThat(result.description()).isEqualTo(updatedEventDto.description()),
            () -> assertThat(result.date()).isEqualTo(updatedEventDto.date()),
            () -> assertThat(result.labels().equals(updatedEventDto.labels())),
            () -> assertThat(result.sharedFlat().getId()).isEqualTo(updatedEventDto.sharedFlat().getId())
        );
    }

    @Test
    @DisplayName("Negative test for updating an existing event with more thant 3 labels")
    void givenUpdatedEventWithMoreLabelsShouldThrowValidationException() throws AuthorizationException, ValidationException {

        EventLabelDto label1 = EventLabelDtoBuilder.builder()
            .labelName("label1")
            .build();
        EventLabelDto label2 = EventLabelDtoBuilder.builder()
            .labelName("label2")
            .build();
        EventLabelDto label3 = EventLabelDtoBuilder.builder()
            .labelName("label3")
            .build();
        EventLabelDto label4 = EventLabelDtoBuilder.builder()
            .labelName("label4")
            .build();
        List<EventLabelDto> labels = new ArrayList<>();
        labels.addAll(List.of(label1, label2, label3, label4));

        //given
        EventDto updatedEventDto = EventDtoBuilder.builder()
            .id(1L)
            .title("Updated Title")
            .description("Updated Description")
            .date(LocalDate.now().plusDays(2))
            .startTime(LocalTime.of(16,0))
            .endTime(LocalTime.of(17,0))
            .labels(labels)
            .sharedFlat(sharedFlatMapper.entityToWgDetailDto(new SharedFlat().setId(1L)))
            .build();

        assertThrows(ValidationException.class, () -> eventsService.update(updatedEventDto));


    }

    @Test
    @DisplayName("Given Label Name Return List with All Events with This Label")
    void givenLabelNameReturnListWithAllEventsWithThisLabel() throws AuthorizationException {
        String labelName = "party";

        List<EventDto> events = eventsService.findEventsByLabel(labelName);

        assertThat(events.size()).isEqualTo(2);
    }
}
