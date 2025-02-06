package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

import java.util.List;

/**
 * Service interface for managing events.
 */
public interface EventsService {

    /**
     * Creates a new event.
     *
     * @param event The EventDto object representing the new event.
     * @return The created EventDto.
     * @throws ValidationException If the event validation fails.
     */
    EventDto create(EventDto event) throws ValidationException;

    /**
     * Updates an existing event.
     *
     * @param event The EventDto object representing the updated event.
     * @return The updated EventDto.
     * @throws AuthorizationException If the user is not authorized to update the event.
     * @throws ValidationException If the event validation fails.
     */
    EventDto update(EventDto event) throws AuthorizationException, ValidationException;

    /**
     * Deletes an event by its ID.
     *
     * @param id The ID of the event to be deleted.
     * @return The deleted EventDto.
     * @throws AuthorizationException If the user is not authorized to delete the event.
     */
    EventDto delete(Long id) throws AuthorizationException;

    /**
     * Retrieves a list of all events.
     *
     * @return A list of EventDto objects representing all events.
     */
    List<EventDto> findAll();

    /**
     * Retrieves an event by its ID.
     *
     * @param id The ID of the event to be retrieved.
     * @return The EventDto with the specified ID.
     * @throws AuthorizationException If the user is not authorized to access the event.
     */
    EventDto getEventWithId(Long id) throws AuthorizationException;

    /**
     * Retrieves all events with given label.
     *
     * @param labelName The name of the label to be retrieved.
     * @return A list of EventDto objects representing all events with given label.
     * @throws AuthorizationException If the user is not authorized to access the events.
     */
    List<EventDto> findEventsByLabel(String labelName) throws AuthorizationException;

    /**
     * Creates a string for an ics-File for all events.
     *
     * @return A string  representing all information for an ics-File for all events.
     */
    String exportAll();

    /**
     * Creates a string for an ics-File for an event with given id.
     *
     * @return A string  representing all information for an ics-File for a single event.
     * @throws AuthorizationException If the user is not authorized to access the events.
     */
    String exportEvent(Long id) throws AuthorizationException;
}

