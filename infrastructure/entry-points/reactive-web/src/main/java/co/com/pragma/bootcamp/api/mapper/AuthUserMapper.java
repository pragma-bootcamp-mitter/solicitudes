package co.com.pragma.bootcamp.api.mapper;

import co.com.pragma.bootcamp.api.dto.AuthUserDto;
import co.com.pragma.bootcamp.model.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthUserMapper {
    User toDomain(AuthUserDto dto);
}
