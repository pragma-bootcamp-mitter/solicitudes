package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.dto.ApplicationRequest;
import co.com.pragma.bootcamp.api.dto.ApplicationResponse;
import co.com.pragma.bootcamp.api.helper.ValidatorUtil;
import co.com.pragma.bootcamp.api.mapper.ApplicationMapper;
import co.com.pragma.bootcamp.model.application.Application;
import co.com.pragma.bootcamp.model.application.gateways.ApplicationRepository;
import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.usecase.registrarsolicitud.RegisterApplicationUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationHandlerTest {

    @InjectMocks
    private ApplicationHandler applicationHandler;

    @Mock
    private RegisterApplicationUseCase useCase;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ApplicationMapper mapper;

    @Mock
    private ValidatorUtil validatorUtil;

    @Mock
    private ServerRequest serverRequest;

    private ApplicationRequest request;
    private Application domain;
    private Application saved;
    private ApplicationResponse response;

    private Application testApplicationDomain;
    private ApplicationRequest testApplicationRequest;
    private ApplicationResponse testApplicationResponse;

    @BeforeEach
    void setUp() {
        testApplicationRequest = new ApplicationRequest();
        testApplicationRequest.setClientDocument("123456789");
        testApplicationRequest.setAmount(BigDecimal.valueOf(1000));
        testApplicationRequest.setTermMonths(12);
        testApplicationRequest.setEmail("test@test.com");
        testApplicationRequest.setLoanType(new ApplicationRequest.LoanTypeRequest());
        testApplicationRequest.getLoanType().setId(1);

        testApplicationDomain = Application.builder()
                .id("app-123")
                .clientDocument("123456789")
                .amount(BigDecimal.valueOf(1000))
                .termMonths(12)
                .email("test@test.com")
                .build();

        testApplicationResponse = ApplicationResponse.builder()
                .id("app-123")
                .clientDocument("123456789")
                .amount(BigDecimal.valueOf(1000))
                .termMonths(12)
                .build();
    }

    @Test
    void register_shouldReturnCreated_whenSuccessful() {
        // Given
        when(serverRequest.bodyToMono(ApplicationRequest.class)).thenReturn(Mono.just(testApplicationRequest));
        when(validatorUtil.validate(testApplicationRequest)).thenReturn(Mono.just(testApplicationRequest));
        when(mapper.toDomain(testApplicationRequest)).thenReturn(testApplicationDomain);
        when(useCase.register(testApplicationDomain)).thenReturn(Mono.just(testApplicationDomain));
        when(mapper.toResponse(testApplicationDomain)).thenReturn(testApplicationResponse);

        // When
        Mono<ServerResponse> responseMono = applicationHandler.register(serverRequest);

        // Then
        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode().equals(HttpStatus.CREATED)
                )
                .verifyComplete();
    }

    @Test
    void register_shouldReturnBadRequest_whenValidationFails() {
        // Given
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        ApplicationRequest invalidApplicationRequest = new ApplicationRequest();
        Set<ConstraintViolation<ApplicationRequest>> violations = validator.validate(invalidApplicationRequest);
        ConstraintViolationException validationException = new ConstraintViolationException(violations);

        when(serverRequest.bodyToMono(ApplicationRequest.class)).thenReturn(Mono.just(invalidApplicationRequest));
        when(validatorUtil.validate(invalidApplicationRequest)).thenReturn(Mono.error(validationException));

        // When
        Mono<ServerResponse> responseMono = applicationHandler.register(serverRequest);

        // Then
        StepVerifier.create(responseMono)
                .expectErrorMatches(ConstraintViolationException.class::isInstance)
                .verify();
    }

    @Test
    void list_shouldReturnOk_whenApplicationsExist() {
        // Given
        when(applicationRepository.findAll()).thenReturn(Flux.just(testApplicationDomain));
        when(mapper.toResponse(testApplicationDomain)).thenReturn(testApplicationResponse);

        // When
        Mono<ServerResponse> responseMono = applicationHandler.list(serverRequest);

        // Then
        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode().equals(HttpStatus.OK)
                )
                .verifyComplete();
    }


    @Test
    void list_shouldReturnOk_whenNoApplicationsExist() {
        // Given
        when(applicationRepository.findAll()).thenReturn(Flux.empty());

        // When
        Mono<ServerResponse> responseMono = applicationHandler.list(serverRequest);

        // Then
        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode().equals(HttpStatus.OK)
                )
                .verifyComplete();
    }
}