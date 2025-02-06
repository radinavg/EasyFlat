package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public abstract class UserMapper {

    @Mapping(source = "applicationUser.sharedFlat.name", target = "flatName")
    @Mapping(source = "applicationUser.id", target = "id")
    public abstract UserDetailDto entityToUserDetailDto(ApplicationUser applicationUser);

    @Mapping(source = "applicationUser.sharedFlat.name", target = "flatName")
    @Mapping(source = "applicationUser.id", target = "id")
    public abstract List<UserDetailDto> entityListToUserDetailDtoList(List<ApplicationUser> users);

    public abstract UserListDto entityToUserListDto(ApplicationUser applicationUser);

    public abstract List<UserListDto> entityListToUserListDto(List<ApplicationUser> applicationUsers);

}
