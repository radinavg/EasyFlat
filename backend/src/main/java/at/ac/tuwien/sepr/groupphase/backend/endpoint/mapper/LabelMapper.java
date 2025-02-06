package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemLabelDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemLabel;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public abstract class LabelMapper {


    public abstract List<ItemLabel> dtoListToEntityList(List<ItemLabelDto> newLabels);

    public abstract List<ItemLabelDto> itemLabelListToItemLabelDtoList(List<ItemLabel> labels);

}
