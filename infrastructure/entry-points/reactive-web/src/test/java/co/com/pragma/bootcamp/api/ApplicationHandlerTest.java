package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.dto.ApplicationRequest;
import co.com.pragma.bootcamp.api.dto.ApplicationResponse;
import co.com.pragma.bootcamp.api.dto.ApplicationSummaryResponse;
import co.com.pragma.bootcamp.api.helper.ValidatorUtil;
import co.com.pragma.bootcamp.api.mapper.ApplicationMapper;
import co.com.pragma.bootcamp.model.application.Application;
import co.com.pragma.bootcamp.model.applicationsummary.ApplicationSummary;
import co.com.pragma.bootcamp.model.applicationsummary.PageModel;
import co.com.pragma.bootcamp.usecase.listapplications.ListApplicationsUseCase;
import co.com.pragma.bootcamp.usecase.registerapplication.ApplicationUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import java.util.Collections;
import java.util.Set;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ApplicationHandlerTest {

    @InjectMocks
    private ApplicationHandler applicationHandler;

    @Mock
    private ListApplicationsUseCase listUseCase;

    @Mock
    private ApplicationUseCase useCase;

    @Mock
    private ApplicationMapper mapper;

    @Mock
    private ValidatorUtil validatorUtil;

    @Mock
    private ServerRequest serverRequest;

    private Application testApplicationDomain;
    private ApplicationRequest testApplicationRequest;
    private ApplicationResponse testApplicationResponse;
    private ApplicationSummary testApplicationSummary;
    private ApplicationSummaryResponse testApplicationSummaryResponse;

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

        testApplicationSummary = ApplicationSummary.builder()
                .id("app-123")
                .amount(BigDecimal.valueOf(1000))
                .termMonths(12)
                .email("test@test.com")
                .clientName("Test User")
                .loanTypeName("Automobile Loan")
                .interestRate(BigDecimal.valueOf(0.05))
                .stateName("PENDING_REVIEW")
                .baseSalary(BigDecimal.valueOf(5000))
                .totalMonthlyDebt(BigDecimal.valueOf(1000))
                .build();

        testApplicationSummaryResponse = ApplicationSummaryResponse.builder()
                .id("app-123")
                .amount(BigDecimal.valueOf(1000))
                .termMonths(12)
                .email("test@test.com")
                .clientName("Test User")
                .loanTypeName("Automobile Loan")
                .interestRate(BigDecimal.valueOf(0.05))
                .stateName("PENDING_REVIEW")
                .baseSalary(BigDecimal.valueOf(5000))
                .totalMonthlyDebt(BigDecimal.valueOf(1000))
                .build();
    }

    @Test
    void register_shouldReturnCreated_whenSuccessful() {
        String authenticatedDocument = "101";
        String authenticatedRole = "ADMIN";
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                authenticatedDocument, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + authenticatedRole))
        );
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(serverRequest.bodyToMono(ApplicationRequest.class)).thenReturn(Mono.just(testApplicationRequest));
        when(validatorUtil.validate(any(ApplicationRequest.class))).thenReturn(Mono.just(testApplicationRequest));
        when(mapper.toDomain(any(ApplicationRequest.class))).thenReturn(testApplicationDomain);
        when(useCase.register(any(Application.class), any(String.class), any(String.class)))
                .thenReturn(Mono.just(testApplicationDomain));
        when(mapper.toResponse(any(Application.class))).thenReturn(testApplicationResponse);

        Mono<ServerResponse> responseMono = Mono.defer(() -> applicationHandler.register(serverRequest))
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));

        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().equals(HttpStatus.CREATED))
                .verifyComplete();
    }

    @Test
    void register_shouldReturnBadRequest_whenValidationFails() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        ApplicationRequest invalidApplicationRequest = new ApplicationRequest();
        Set<ConstraintViolation<ApplicationRequest>> violations = validator.validate(invalidApplicationRequest);
        ConstraintViolationException validationException = new ConstraintViolationException(violations);

        String authenticatedDocument = "101";
        String authenticatedRole = "ADMIN";
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                authenticatedDocument, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + authenticatedRole))
        );
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(serverRequest.bodyToMono(ApplicationRequest.class)).thenReturn(Mono.just(invalidApplicationRequest));
        when(validatorUtil.validate(any(ApplicationRequest.class))).thenReturn(Mono.error(validationException));

        Mono<ServerResponse> responseMono = Mono.defer(() -> applicationHandler.register(serverRequest))
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));

        StepVerifier.create(responseMono)
                .expectErrorMatches(ConstraintViolationException.class::isInstance)
                .verify();
    }

    @Test
    void list_shouldReturnOk_whenApplicationsExist() {
        // Mockear el nuevo comportamiento: devolver un Mono<PageModel>
        PageModel<ApplicationSummary> pageModel = PageModel.<ApplicationSummary>builder()
                .content(List.of(testApplicationSummary))
                .page(0)
                .size(10)
                .totalElements(1L)
                .totalPages(1)
                .build();
        when(listUseCase.listByState(anyInt(), anyInt(), anyString()))
                .thenReturn(Mono.just(pageModel));

        // Mockear el mapper
        when(mapper.toSummaryResponse(any(ApplicationSummary.class))).thenReturn(testApplicationSummaryResponse);

        // Mockear los parámetros de la request
        when(serverRequest.queryParam("stateName")).thenReturn(Optional.of("PENDING_REVIEW"));
        when(serverRequest.queryParam("page")).thenReturn(Optional.of("0"));
        when(serverRequest.queryParam("size")).thenReturn(Optional.of("10"));

        Mono<ServerResponse> responseMono = applicationHandler.list(serverRequest);

        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode().equals(HttpStatus.OK)
                )
                .verifyComplete();
    }

    @Test
    void list_shouldReturnOk_whenNoApplicationsExist() {
        // Mockear el nuevo comportamiento con una lista vacía
        PageModel<ApplicationSummary> emptyPageModel = PageModel.<ApplicationSummary>builder()
                .content(List.of())
                .page(0)
                .size(10)
                .totalElements(0L)
                .totalPages(0)
                .build();
        when(listUseCase.listByState(anyInt(), anyInt(), anyString()))
                .thenReturn(Mono.just(emptyPageModel));

        // Mockear los parámetros de la request
        when(serverRequest.queryParam("stateName")).thenReturn(Optional.of("PENDING_REVIEW"));
        when(serverRequest.queryParam("page")).thenReturn(Optional.of("0"));
        when(serverRequest.queryParam("size")).thenReturn(Optional.of("10"));

        Mono<ServerResponse> responseMono = applicationHandler.list(serverRequest);

        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode().equals(HttpStatus.OK)
                )
                .verifyComplete();
    }
}