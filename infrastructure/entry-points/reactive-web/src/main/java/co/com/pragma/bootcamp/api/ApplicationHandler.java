package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.dto.ApplicationRequest;
import co.com.pragma.bootcamp.api.dto.ApiResponse;
import co.com.pragma.bootcamp.api.helper.ValidatorUtil;
import co.com.pragma.bootcamp.api.mapper.ApplicationMapper;
import co.com.pragma.bootcamp.model.application.gateways.ApplicationRepository;
import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.usecase.registrarsolicitud.RegisterApplicationUseCase;
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

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationHandler {

    private final RegisterApplicationUseCase useCase;
    private final ApplicationRepository applicationRepository;
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

    @SuppressWarnings("unused")
    public Mono<ServerResponse> list(ServerRequest request) {
        return applicationRepository.findAll()
                .map(mapper::toResponse)
                .collectList()
                .flatMap(list ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ApiResponse.success(list))
                );
    }

}