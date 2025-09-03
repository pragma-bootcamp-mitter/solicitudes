package co.com.pragma.bootcamp.r2dbc.mapper;

import co.com.pragma.bootcamp.model.application.Application;
import co.com.pragma.bootcamp.r2dbc.entity.ApplicationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ApplicationEntityMapper {
    @Mapping(target = "stateId", source = "stateId")
    @Mapping(target = "loanTypeId", source = "loanTypeId")
    Application toDomain(ApplicationEntity applicationEntity);

    @Mapping(target = "stateId", source = "stateId")
    @Mapping(target = "loanTypeId", source = "loanTypeId")
    ApplicationEntity toEntity(Application application);
}
