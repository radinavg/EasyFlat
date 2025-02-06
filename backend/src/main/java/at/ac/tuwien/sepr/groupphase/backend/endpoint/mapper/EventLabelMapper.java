package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventLabelDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.EventLabel;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public abstract class EventLabelMapper {

    public abstract List<EventLabel> dtoListToEntityList(List<EventLabelDto> labels);

    public abstract List<EventLabelDto> itemLabelListToItemLabelDtoList(List<EventLabel> labels);
}
