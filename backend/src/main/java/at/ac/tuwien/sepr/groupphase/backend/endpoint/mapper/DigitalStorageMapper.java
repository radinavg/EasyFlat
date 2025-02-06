package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = SharedFlatMapper.class)
public abstract class DigitalStorageMapper {


    @Mapping(target = "itemList", ignore = true)
    public abstract DigitalStorage dtoToEntity(DigitalStorageDto digitalStorageDto);

    public abstract DigitalStorageDto entityToDto(DigitalStorage digitalStorage);

    public abstract List<DigitalStorageDto> entityListToDtoList(List<DigitalStorage> digitalStorageDtoList);


}
