package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventLabelDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventLabelDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeSuggestionDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SharedFlatMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.core.type.TypeReference;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class EventEndpointTest {
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

    private final String BASE_URI = "/api/v1/events";
    private final String EXPORT_BASE_URI = "/api/v1/events/export";
    private ApplicationUser applicationUser;
    @Autowired
    private SharedFlatMapper sharedFlatMapper;

    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        testDataGenerator.cleanUp();

        applicationUser = userRepository.findById(1L).orElseThrow();
        when(authService.getUserFromToken()).thenReturn(applicationUser);
    }

    @Test
    public void givenEventWhenCreateThenEventIsCreated() throws Exception {
        // given
        EventDto eventDto = new EventDto(null, "Test Event", "Description", LocalTime.now(),LocalTime.now().plusMinutes(30),LocalDate.now().plusDays(1), null,new ArrayList<>());


        // when
        MvcResult mvcResult = this.mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto))
                .header("Authorization", "Bearer " + jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        // then
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        EventDto createdEvent = objectMapper.readValue(response.getContentAsString(), EventDto.class);

        assertAll(
            () -> assertNotNull(createdEvent.id()),
            () -> assertEquals(eventDto.title(), createdEvent.title()),
            () -> assertEquals(eventDto.description(), createdEvent.description()),
            () -> assertEquals(eventDto.date(), createdEvent.date())
        );
    }

    @Test
    public void givenInvalidEventWhenCreateThenValidationException() throws Exception {
        // given
        EventDto invalidEventDto = new EventDto(null, "", "",LocalTime.now(),LocalTime.now().plusHours(2), LocalDate.now(), null,new ArrayList<>());

        // when
        MvcResult mvcResult = this.mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidEventDto))
                .header("Authorization", "Bearer " + jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        // then
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());


    }

    @Test
    public void givenNoEventsWhenGetEventsThenReturnEmptyList() throws Exception {


        // when
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andReturn();

        // then
        assertEquals(200, mvcResult.getResponse().getStatus());

        String responseContent = mvcResult.getResponse().getContentAsString();
        List<EventDto> events = objectMapper.readValue(responseContent, new TypeReference<List<EventDto>>() {});

        assertThat(events).isNotEmpty();
    }

    @Test
    public void givenEventIdWhenUpdateThenEventIsUpdated() throws Exception {
        // given
        Long eventId = 1L;
        EventDto updatedEventDto = new EventDto(eventId, "Updated Event", "Updated Description", LocalTime.now(),LocalTime.now().plusMinutes(30),LocalDate.now().plusDays(1), null,new ArrayList<>());

        // when
        MvcResult mvcResult = this.mockMvc.perform(put(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEventDto))
                .header("Authorization", "Bearer " + jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        // then
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        EventDto updatedEvent = objectMapper.readValue(response.getContentAsString(), EventDto.class);

        assertAll(
            () -> assertNotNull(updatedEvent.id()),
            () -> assertEquals(updatedEventDto.title(), updatedEvent.title()),
            () -> assertEquals(updatedEventDto.description(), updatedEvent.description()),
            () -> assertEquals(updatedEventDto.date(), updatedEvent.date())
        );
    }

    @Test
    public void givenEventIdWhenDeleteThenEventIsDeleted() throws Exception {
        // given
        Long eventId = 1L;

        // when
        MvcResult mvcResult = this.mockMvc.perform(delete(BASE_URI + "/{id}", eventId)
                .header("Authorization", "Bearer " + jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        // then
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        EventDto deletedEvent = objectMapper.readValue(response.getContentAsString(), EventDto.class);

        assertAll(
            () -> assertNotNull(deletedEvent.id()),
            () -> assertEquals(eventId, deletedEvent.id())
        );
    }
    @Test
    public void givenInvalidEventWhenUpdateThenValidationException() throws Exception {
        // given
        EventDto invalidEventDto = new EventDto(1L, "", "", LocalTime.now(),LocalTime.now().plusHours(2),LocalDate.now().plusDays(1), null,new ArrayList<>());

        // when
        MvcResult mvcResult = this.mockMvc.perform(put(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidEventDto))
                .header("Authorization", "Bearer " + jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        // then
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

    @Test
    public void givenNonExistingEventIdWhenDeleteThenEventNotFound() throws Exception {
        // given
        Long nonExistingEventId = -1000L;

        // when
        MvcResult mvcResult = this.mockMvc.perform(delete(BASE_URI + "/{id}", nonExistingEventId)
                .header("Authorization", "Bearer " + jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        // then
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }



    @Test
    public void givenExistingEventIdWhenGetEventWithIdThenReturnEvent() throws Exception {
        // given
        Long eventId = 1L;
        SharedFlat sharedFlat = new SharedFlat().setId(1L).setName("Shared Flat 1");
        EventDto eventDto = new EventDto(eventId, "House Meeting", "Discussing important matters regarding the shared living space.", LocalTime.now(),LocalTime.now().plusHours(2),LocalDate.now().plusDays(7), sharedFlatMapper.entityToWgDetailDto(sharedFlat),new ArrayList<>());


        // when
        MvcResult mvcResult = this.mockMvc.perform(get(BASE_URI + "/{id}", eventId)
                .header("Authorization", "Bearer " + jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        // then
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        EventDto retrievedEvent = objectMapper.readValue(response.getContentAsString(), EventDto.class);

        assertAll(
            () -> assertNotNull(retrievedEvent.id()),
            () -> assertEquals(eventDto.title(), retrievedEvent.title()),
            () -> assertEquals(eventDto.description(), retrievedEvent.description()),
            () -> assertEquals(eventDto.date(), retrievedEvent.date()),
            () -> assertEquals(eventDto.sharedFlat().getName(), retrievedEvent.sharedFlat().getName())
        );
    }

    @Test
    public void givenValidEventIdWhenGetEventWithIdThenReturnEvent() throws Exception {
        // given
        Long validEventId = 1L;

        // when
        MvcResult mvcResult = this.mockMvc.perform(get(BASE_URI + "/{id}", validEventId)
                .header("Authorization", "Bearer " + jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        // then
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        EventDto retrievedEvent = objectMapper.readValue(response.getContentAsString(), EventDto.class);

        assertAll(
            () -> assertNotNull(retrievedEvent.id()),
            () -> assertEquals(validEventId, retrievedEvent.id())
        );
    }

    @Test
    public void givenEventWithInvalidDateWhenCreateThenValidationException() throws Exception {
        // given
        EventDto invalidDateEvent = new EventDto(null, "Invalid Date Event", "Description", LocalTime.now(),LocalTime.now(),LocalDate.now().minusDays(22), null,new ArrayList<>());

        // when
        MvcResult mvcResult = this.mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDateEvent))
                .header("Authorization", "Bearer " + jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        // then
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

    @Test
    @DisplayName("Negative test for events with non-valid labels with status 422")
    public void givenEventWithInvalidLabelsShouldReturnStatus422() throws Exception {

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

        EventDto invalidDateEvent = new EventDto(null, "Invalid Date Event", "Description", LocalTime.now(),LocalTime.now(),LocalDate.now().minusDays(22), null,labels);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDateEvent))
                .header("Authorization", "Bearer " + jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        // then
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

    @Test
    @DisplayName("Positive test for exporting event with valid id with status 200")
    public void givenValidEventIdForExportShouldReturnStatus200() throws Exception {
        // given
        Long validEventId = 1L;

        // when
        MvcResult mvcResult = this.mockMvc.perform(get(EXPORT_BASE_URI + "/{id}", validEventId)
                .header("Authorization", "Bearer " + jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        // then
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        String retrievedExport = response.getContentAsString();

        assertAll(
            () -> assertNotNull(retrievedExport),
            () -> assertTrue(retrievedExport.startsWith("BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:-//EasyFlat//\n")),
            () -> assertTrue(retrievedExport.contains("House Meeting")),
            () -> assertTrue(retrievedExport.contains("Discussing important matters regarding the shared living space.")),
            () -> assertTrue(retrievedExport.endsWith("END:VCALENDAR"))
        );
    }

    @Test
    @DisplayName("Positive test for all events with status 200")
    public void exportShouldReturnStatus200() throws Exception {

        // when
        MvcResult mvcResult = this.mockMvc.perform(get(EXPORT_BASE_URI)
                .header("Authorization", "Bearer " + jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        // then
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        String retrievedExport = response.getContentAsString();

        assertAll(
            () -> assertNotNull(retrievedExport),
            () -> assertTrue(retrievedExport.startsWith("BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:-//EasyFlat//\n")),
            () -> assertTrue(retrievedExport.contains("House Meeting")),
            () -> assertTrue(retrievedExport.contains("Discussing important matters regarding the shared living space.")),
            () -> assertTrue(retrievedExport.contains("Cleaning Day")),
            () -> assertTrue(retrievedExport.contains("A day dedicated to cleaning and maintaining the shared areas.")),
            () -> assertTrue(retrievedExport.contains("Movie Night")),
            () -> assertTrue(retrievedExport.contains("Gathering for a cozy movie night in the common area.")),
            () -> assertTrue(retrievedExport.endsWith("END:VCALENDAR"))
        );
    }

    @Test
    public void givenLabelNameReturnListWithAllEventsWithThisLabel() throws Exception {
        // given
        String labelName = "party";

        // when
        MvcResult mvcResult = this.mockMvc.perform(get(BASE_URI + "/search")
                .param("label", labelName)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        // then
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        List<EventDto> events = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<EventDto>>() {});


        assertThat(events.size()).isEqualTo(2);
    }


}
