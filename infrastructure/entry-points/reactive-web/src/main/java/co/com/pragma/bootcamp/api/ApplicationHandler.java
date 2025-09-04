package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.dto.ApplicationRequest;
import co.com.pragma.bootcamp.api.dto.ApiResponse;
import co.com.pragma.bootcamp.api.dto.ApplicationSummaryResponse;
import co.com.pragma.bootcamp.api.helper.ValidatorUtil;
import co.com.pragma.bootcamp.api.mapper.ApplicationMapper;
import co.com.pragma.bootcamp.model.applicationsummary.ApplicationSummary;
import co.com.pragma.bootcamp.usecase.listapplications.ListApplicationsUseCase;
import co.com.pragma.bootcamp.usecase.registerapplication.ApplicationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import java.util.List;

import static co.com.pragma.bootcamp.usecase.registerapplication.helper.DomainConstants.PENDING_REVIEW_STATE;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationHandler {

    public static final String SIZE = "size";
    public static final String PAGE = "page";
    public static final String STATE_NAME = "stateName";
    private final ApplicationUseCase useCase;
    private final ListApplicationsUseCase listUseCase;
    private final ApplicationMapper mapper;
    private final ValidatorUtil validatorUtil;
    private static final String ROLE_PREFIX = "ROLE_";

    public Mono<ServerResponse> register(ServerRequest request) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    String authenticatedDocument = (String) authentication.getPrincipal();
                    String authenticatedRole = authentication.getAuthorities().iterator().next().getAuthority().replace(ROLE_PREFIX, "");
                    return request.bodyToMono(ApplicationRequest.class)
                            .flatMap(validatorUtil::validate)
                            .map(mapper::toDomain)
                            .flatMap(application -> useCase.register(application, authenticatedDocument, authenticatedRole))
                            .map(mapper::toResponse)
                            .flatMap(response ->
                                    ServerResponse.status(HttpStatus.CREATED)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .bodyValue(ApiResponse.success(response))
                            );
                });
    }

    public Mono<ServerResponse> list(ServerRequest request) {
        int size = request.queryParam(SIZE).map(Integer::parseInt).orElse(10);
        int page = request.queryParam(PAGE).map(Integer::parseInt).orElse(0);
        String stateName = request.queryParam(STATE_NAME).orElse(PENDING_REVIEW_STATE);
        return listUseCase.listByState(size, page, stateName)
                .flatMap(tuple -> {
                    List<ApplicationSummary> applications = tuple.getT1();
                    Long totalElements = tuple.getT2();
                    List<ApplicationSummaryResponse> responseList = applications.stream()
                            .map(mapper::toSummaryResponse)
                            .toList();
                    ApiResponse<List<ApplicationSummaryResponse>> apiResponse = ApiResponse.success(
                            responseList,
                            page,
                            size,
                            totalElements);
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(apiResponse);
                });
    }
}