package co.com.pragma.bootcamp.r2dbc.mapper;

import co.com.pragma.bootcamp.model.application.Application;
import co.com.pragma.bootcamp.r2dbc.entity.ApplicationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ApplicationEntityMapper {
    @Mapping(target = "stateId", source = "state.id")
    @Mapping(target = "loanTypeId", source = "loanType.id")
    ApplicationEntity toEntity(Application application);

    @Mapping(target = "state.id", source = "stateId")
    @Mapping(target = "loanType.id", source = "loanTypeId")
    Application toDomain(ApplicationEntity applicationEntity);
}
