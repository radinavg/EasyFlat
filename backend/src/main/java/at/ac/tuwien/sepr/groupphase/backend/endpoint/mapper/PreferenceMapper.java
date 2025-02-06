package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PreferenceDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Chore;
import at.ac.tuwien.sepr.groupphase.backend.entity.Preference;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChoreRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Mapper
public abstract class PreferenceMapper {
    @Mapping(target = "first", source = "preferenceDto.first")
    @Mapping(target = "second", source = "preferenceDto.second")
    @Mapping(target = "third", source = "preferenceDto.third")
    @Mapping(target = "fourth", source = "preferenceDto.fourth")
    public abstract Preference preferenceDtoToEntity(PreferenceDto preferenceDto);

    @Mapping(target = "first", source = "preference.first")
    @Mapping(target = "second", source = "preference.second")
    @Mapping(target = "third", source = "preference.third")
    @Mapping(target = "fourth", source = "preference.fourth")
    public abstract PreferenceDto entityToPreferenceDto(Preference preference);


}
