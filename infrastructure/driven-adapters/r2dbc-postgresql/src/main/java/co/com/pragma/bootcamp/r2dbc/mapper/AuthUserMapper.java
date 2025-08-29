package co.com.pragma.bootcamp.r2dbc.mapper;


import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.r2dbc.entity.UserAuth;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthUserMapper {
    User toDomain(UserAuth dto);
}
