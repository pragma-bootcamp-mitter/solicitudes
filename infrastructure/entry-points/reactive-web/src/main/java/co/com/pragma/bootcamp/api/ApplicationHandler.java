package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.dto.ApplicationRequest;
import co.com.pragma.bootcamp.api.dto.ApiResponse;
import co.com.pragma.bootcamp.api.helper.ValidatorUtil;
import co.com.pragma.bootcamp.api.mapper.ApplicationMapper;
import co.com.pragma.bootcamp.model.application.gateways.ApplicationRepository;
import co.com.pragma.bootcamp.model.applicationsummary.Pagination;
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
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationHandler {

    private final ApplicationUseCase useCase;
    private final ApplicationRepository applicationRepository;
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
        Optional<String> stateNameParam = request.queryParam("stateName");
        Optional<String> pageParam = request.queryParam("page");
        Optional<String> sizeParam = request.queryParam("size");

        int page = pageParam.map(Integer::parseInt).orElse(0);
        int size = sizeParam.map(Integer::parseInt).orElse(10);
        String stateName = stateNameParam.orElse("PENDING_REVIEW");

        Pagination pagination = Pagination.builder()
                .page(page)
                .size(size)
                .build();

        return listUseCase.listAll(pagination, stateName)
                .map(mapper::toSummaryResponse)
                .collectList()
                .flatMap(list ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ApiResponse.success(list))
                );
    }

}