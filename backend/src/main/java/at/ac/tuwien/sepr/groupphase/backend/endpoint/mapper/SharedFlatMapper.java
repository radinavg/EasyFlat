package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper
public abstract class SharedFlatMapper {
    public abstract WgDetailDto entityToWgDetailDto(SharedFlat sharedFlat);

    @Mapping(target = "users", ignore = true)
    @Mapping(target = "digitalStorage", ignore = true)
    public abstract SharedFlat wgDetailDtoToEntity(WgDetailDto wgDetailDto);
}
