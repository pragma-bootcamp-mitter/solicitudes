package co.com.pragma.bootcamp.api.mapper;

import co.com.pragma.bootcamp.api.dto.ApplicationRequest;
import co.com.pragma.bootcamp.api.dto.ApplicationResponse;
import co.com.pragma.bootcamp.model.application.Application;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    // Request -> Domain
    @Mapping(target = "state.id", source = "state.id")
    @Mapping(target = "loanType.id", source = "loanType.id")
    Application toDomain(ApplicationRequest request);

    // Domain -> Response
    @Mapping(target = "stateId", source = "state.id")
    @Mapping(target = "loanTypeId", source = "loanType.id")
    ApplicationResponse toResponse(Application application);
}

