package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.entity.EventLabel;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public abstract class EventMapper {

    @Mapping(target = "labels", expression = "java( labels )")
    public abstract Event dtoToEntity(EventDto eventDto, @Context List<EventLabel> labels);

    @Mapping(source = "sharedFlat", target = "sharedFlat")
    @Mapping(source = "labels", target = "labels")
    public abstract EventDto entityToDto(Event event, @Context WgDetailDto sharedFlat);
}
