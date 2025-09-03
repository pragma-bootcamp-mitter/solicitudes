package co.com.pragma.bootcamp.r2dbc.mapper;

import co.com.pragma.bootcamp.model.state.State;
import co.com.pragma.bootcamp.r2dbc.entity.StateEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StateMapper {
    @Mapping(source = "stateId", target = "id")
    State toDomain(StateEntity stateEntity);
    @Mapping(source = "id", target = "stateId")
    StateEntity toEntity(State state);
}
