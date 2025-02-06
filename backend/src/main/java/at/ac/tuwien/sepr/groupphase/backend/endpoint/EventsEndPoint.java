package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.EventsService;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
public class EventsEndPoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final EventsService eventsService;

    public EventsEndPoint(EventsService eventsService) {
        this.eventsService = eventsService;
    }

    @PermitAll
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public EventDto create(@RequestBody EventDto event) throws ValidationException {

        return eventsService.create(event);
    }

    @Secured("ROLE_USER")
    @PutMapping
    public EventDto update(@RequestBody EventDto event) throws AuthorizationException, ValidationException {
        return eventsService.update(event);
    }

    @Secured("ROLE_USER")
    @DeleteMapping("{id}")
    public EventDto delete(@PathVariable Long id) throws AuthorizationException {
        return eventsService.delete(id);
    }


    @Secured("ROLE_USER")
    @GetMapping
    public List<EventDto> getEvents() {
        return eventsService.findAll();

    }

    @Secured("ROLE_USER")
    @GetMapping("{id}")
    public EventDto getEventWithId(@PathVariable String id) throws AuthorizationException {
        return eventsService.getEventWithId(Long.valueOf(id));
    }

    @Secured("ROLE_USER")
    @GetMapping("/search")
    public List<EventDto> findEventsByLabel(String label) throws AuthorizationException {
        return eventsService.findEventsByLabel(label);
    }

    @Secured("ROLE_USER")
    @GetMapping("/export")
    public String exportAll() {
        return eventsService.exportAll();
    }

    @Secured("ROLE_USER")
    @GetMapping("/export/{id}")
    public String exportEvent(@PathVariable String id) throws AuthorizationException {
        return eventsService.exportEvent(Long.valueOf(id));
    }
}
