package co.com.pragma.bootcamp.api.mapper;

import co.com.pragma.bootcamp.api.dto.ApplicationRequest;
import co.com.pragma.bootcamp.api.dto.ApplicationResponse;
import co.com.pragma.bootcamp.api.dto.ApplicationSummaryResponse;
import co.com.pragma.bootcamp.model.application.Application;
import co.com.pragma.bootcamp.model.applicationsummary.ApplicationSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    @Mapping(target = "stateId", source = "state.id")
    @Mapping(target = "loanTypeId", source = "loanType.id")
    Application toDomain(ApplicationRequest request);

    @Mapping(target = "stateId", source = "stateId")
    @Mapping(target = "loanTypeId", source = "loanTypeId")
    ApplicationResponse toResponse(Application application);


    ApplicationSummaryResponse toSummaryResponse(ApplicationSummary summary);
}

