package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.dto.ApplicationRequest;
import co.com.pragma.bootcamp.api.dto.ApplicationResponse;
import co.com.pragma.bootcamp.api.mapper.ApplicationMapper;
import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.model.application.Application;
import co.com.pragma.bootcamp.model.application.gateways.ApplicationRepository;
import co.com.pragma.bootcamp.usecase.registrarsolicitud.RegisterApplicationUseCase;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HandlerTest {

    @Mock
    private RegisterApplicationUseCase useCase;

    @Mock
    private ApplicationMapper mapper;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private Validator validator;

    @InjectMocks
    private Handler handler;

    private ApplicationRequest request;
    private Application domain;
    private Application saved;
    private ApplicationResponse response;

    @BeforeEach
    void setUp() {
        request = new ApplicationRequest(
                "123456789",
                BigDecimal.valueOf(1000000),
                12,
                "test@example.com",
                new ApplicationRequest.StateRequest(),
                new ApplicationRequest.LoanTypeRequest()
        );
        request.getState().setId(1);
        request.getLoanType().setId(1);

        domain = Application.builder().id("id-domain").build();
        saved = Application.builder().id("id-saved").build();
        response = ApplicationResponse.builder().id("id-response").build();
    }

    @Test
    void register_debeRetornar201_cuandoEsExitoso() {
        when(mapper.toDomain(any(ApplicationRequest.class))).thenReturn(domain);
        when(useCase.register(any(Application.class))).thenReturn(Mono.just(saved));
        when(mapper.toResponse(any(Application.class))).thenReturn(response);

        ServerRequest mockRequest = MockServerRequest.builder()
                .body(Mono.just(request));

        Mono<ServerResponse> result = handler.register(mockRequest);

        StepVerifier.create(result)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode().equals(HttpStatus.CREATED) &&
                                serverResponse.headers().getContentType().equals(MediaType.APPLICATION_JSON)
                )
                .verifyComplete();
    }

    @Test
    void register_debeRetornar409_cuandoElUseCaseLanzaBusinessException() {
        when(mapper.toDomain(any(ApplicationRequest.class))).thenReturn(domain);
        when(useCase.register(any(Application.class)))
                .thenReturn(Mono.error(new BusinessException("Solicitud duplicada")));

        ServerRequest mockRequest = MockServerRequest.builder()
                .body(Mono.just(request));

        Mono<ServerResponse> result = handler.register(mockRequest);

        StepVerifier.create(result)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode().equals(HttpStatus.CONFLICT) &&
                                serverResponse.headers().getContentType().equals(MediaType.APPLICATION_JSON)
                )
                .verifyComplete();
    }

    @Test
    void register_debeRetornar500_cuandoElMapeoFalla() {
        when(mapper.toDomain(any(ApplicationRequest.class)))
                .thenThrow(new RuntimeException("Error de mapeo"));

        ServerRequest mockRequest = MockServerRequest.builder()
                .body(Mono.just(request));

        Mono<ServerResponse> result = handler.register(mockRequest);

        StepVerifier.create(result)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR) &&
                                serverResponse.headers().getContentType().equals(MediaType.APPLICATION_JSON)
                )
                .verifyComplete();
    }

    @Test
    void register_debeRetornar400_cuandoElBodyEsVacio() {
        ServerRequest mockRequest = MockServerRequest.builder().body(Mono.empty());

        Mono<ServerResponse> result = handler.register(mockRequest);

        StepVerifier.create(result)
                .expectError();
    }


    @Test
    void list_debeRetornar200_cuandoExitoso() {
        when(applicationRepository.findAll()).thenReturn(Flux.just(domain));
        when(mapper.toResponse(domain)).thenReturn(response);

        ServerRequest mockRequest = MockServerRequest.builder().build();

        Mono<ServerResponse> result = handler.list(mockRequest);

        StepVerifier.create(result)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode().is2xxSuccessful() &&
                                serverResponse.headers().getContentType().equals(MediaType.APPLICATION_JSON)
                )
                .verifyComplete();
    }

    @Test
    void list_debeRetornar200_conListaVacia() {
        when(applicationRepository.findAll()).thenReturn(Flux.empty());

        ServerRequest mockRequest = MockServerRequest.builder().build();

        Mono<ServerResponse> result = handler.list(mockRequest);

        StepVerifier.create(result)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode().is2xxSuccessful()
                )
                .verifyComplete();
    }

    @Test
    void list_debeRetornar500_cuandoOcurreError() {
        when(applicationRepository.findAll()).thenReturn(Flux.error(new RuntimeException("DB error")));

        ServerRequest mockRequest = MockServerRequest.builder().build();

        Mono<ServerResponse> result = handler.list(mockRequest);

        StepVerifier.create(result)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode().is5xxServerError() &&
                                serverResponse.headers().getContentType().equals(MediaType.APPLICATION_JSON)
                )
                .verifyComplete();
    }
}