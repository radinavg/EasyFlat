package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.CookbookDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Cookbook;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = SharedFlatMapper.class)
public abstract class CookbookMapper {

    @Mapping(target = "recipes", ignore = true)
    public abstract Cookbook dtoToEntity(CookbookDto cookbookDto);

    public abstract  CookbookDto entityToDto(Cookbook cookbook);

    public abstract List<CookbookDto> entityListToDtoList(List<Cookbook> cookbookList);
}
