package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChoreDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Chore;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public abstract class ChoreMapper {


    public abstract Chore choreDtoToEntity(ChoreDto choreDto);


    public abstract ChoreDto entityToChoreDto(Chore chore);


    public abstract List<ChoreDto> entityListToDtoList(List<Chore> lists);
}
