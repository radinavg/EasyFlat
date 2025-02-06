package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventLabelDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemLabelDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.EventLabel;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemLabel;

import java.util.List;

public interface EventLabelService {

    /**
     * Finds if on object of type {@link EventLabel} exists in the db.
     *
     * @param value of the label searched for
     * @param color of the label searched for
     * @return an object of type {@link EventLabel} if label with given parameters exists in db, or null otherwise
     */
    EventLabel findByValueAndColour(String value, String color);

    /**
     * Stores objects of type {@link EventLabel} in the db.
     *
     * @param newLabels list of objects of type {@link EventLabelDto} which should be stored in the db
     * @return a list of objects of type {@link EventLabel} of labels stored in the db
     */
    List<EventLabel> createAll(List<EventLabelDto> newLabels);
}
