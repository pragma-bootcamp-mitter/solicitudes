package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.dto.ApplicationRequest;
import co.com.pragma.bootcamp.api.dto.ApiResponse;
import co.com.pragma.bootcamp.api.mapper.ApplicationMapper;
import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.model.application.gateways.ApplicationRepository;
import co.com.pragma.bootcamp.usecase.registrarsolicitud.RegisterApplicationUseCase;
import jakarta.validation.ConstraintViolation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import jakarta.validation.Validator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class Handler {

    private final RegisterApplicationUseCase useCase;
    private final ApplicationRepository applicationRepository;
    private final ApplicationMapper mapper;
    private final Validator validator;

    public Mono<ServerResponse> register(ServerRequest request) {
        return request.bodyToMono(ApplicationRequest.class)
                .doOnNext(req -> log.info("Received ApplicationRequest: {}", req))
                .flatMap(applicationRequest -> {
                    Set<ConstraintViolation<ApplicationRequest>> violations = validator.validate(applicationRequest);
                    if (!violations.isEmpty()) {
                        Map<String, String> errors = violations.stream()
                                .collect(Collectors.toMap(
                                        v -> v.getPropertyPath().toString(),
                                        ConstraintViolation::getMessage
                                ));
                        log.warn("Validation errors in request: {}", errors);
                        return ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ApiResponse.error("Validation Error", errors));
                    }

                    return useCase.register(mapper.toDomain(applicationRequest))
                            .doOnNext(saved -> log.info("Application registered in domain: {}", saved))
                            .map(mapper::toResponse)
                            .flatMap(response ->
                                    ServerResponse.status(HttpStatus.CREATED)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .bodyValue(ApiResponse.ok("Application created successfully", response))
                            );
                })
                .onErrorResume(BusinessException.class, e -> {
                    log.error("Business error when registering application: {}", e.getMessage());
                    return ServerResponse.status(HttpStatus.CONFLICT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ApiResponse.error(e.getMessage()));
                })
                .onErrorResume(e -> {
                    log.error("Unexpected error when registering application: {}", e.getMessage(), e);
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ApiResponse.error("Internal Server Error"));
                });
    }

    public Mono<ServerResponse> list(ServerRequest request) {
        return applicationRepository.findAll()
                .map(mapper::toResponse)
                .collectList()
                .flatMap(list ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ApiResponse.ok("List of applications", list))
                )
                .onErrorResume(e -> {
                    log.error("Error listing applications: {}", e.getMessage(), e);
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ApiResponse.error("Internal server error when listing applications"));
                });
    }

}
