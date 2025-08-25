package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.dto.SolicitudRequest;
import co.com.pragma.bootcamp.api.dto.SolicitudResponse;
import co.com.pragma.bootcamp.api.mapper.SolicitudDtoMapper;
import co.com.pragma.bootcamp.model.solicitud.Solicitud;
import co.com.pragma.bootcamp.usecase.registrarsolicitud.RegistrarSolicitudUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HandlerTest {

    @Mock
    private RegistrarSolicitudUseCase useCase;

    @Mock
    private SolicitudDtoMapper mapper;

    @InjectMocks
    private Handler handler;

    private SolicitudRequest request;
    private Solicitud domain;
    private Solicitud saved;
    private SolicitudResponse response;

    @BeforeEach
    void setUp() {
        request = new SolicitudRequest(
                "123456789",
                BigDecimal.valueOf(1000000),
                12,
                "test@example.com",
                new SolicitudRequest.EstadoRequest(),
                new SolicitudRequest.TipoPrestamoRequest()
        );
        request.getEstado().setId(1);
        request.getTipoPrestamo().setId(1);

        domain = Solicitud.builder().id("id-domain").build();
        saved = Solicitud.builder().id("id-saved").build();
        response = SolicitudResponse.builder().id("id-response").build();
    }

    @Test
    void registrar_debeRetornar201_cuandoEsExitoso() {
        // Preparar
        when(mapper.toDomain(any(SolicitudRequest.class))).thenReturn(domain);
        when(useCase.registrar(any(Solicitud.class))).thenReturn(Mono.just(saved));
        when(mapper.toResponse(any(Solicitud.class))).thenReturn(response);

        ServerRequest mockRequest = MockServerRequest.builder()
                .body(Mono.just(request));

        // Actuar
        Mono<ServerResponse> result = handler.registrar(mockRequest);

        // Verificar
        StepVerifier.create(result)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode().is2xxSuccessful() &&
                                serverResponse.headers().getContentType().equals(MediaType.APPLICATION_JSON)
                )
                .verifyComplete();
    }

    @Test
    void registrar_debeRetornar400_cuandoElUseCaseFalla() {
        // Preparar
        when(mapper.toDomain(any(SolicitudRequest.class))).thenReturn(domain);
        when(useCase.registrar(any(Solicitud.class)))
                .thenReturn(Mono.error(new IllegalArgumentException("Monto inválido")));

        ServerRequest mockRequest = MockServerRequest.builder()
                .body(Mono.just(request));

        // Actuar
        Mono<ServerResponse> result = handler.registrar(mockRequest);

        // Verificar
        StepVerifier.create(result)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode().is4xxClientError() &&
                                serverResponse.headers().getContentType().equals(MediaType.APPLICATION_JSON)
                )
                .verifyComplete();
    }

    @Test
    void registrar_debeRetornar400_cuandoElMapeoFalla() {
        // Preparar
        when(mapper.toDomain(any(SolicitudRequest.class)))
                .thenThrow(new IllegalArgumentException("Error de mapeo"));

        ServerRequest mockRequest = MockServerRequest.builder()
                .body(Mono.just(request));

        // Actuar
        Mono<ServerResponse> result = handler.registrar(mockRequest);

        // Verificar
        StepVerifier.create(result)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode().is4xxClientError() &&
                                serverResponse.headers().getContentType().equals(MediaType.APPLICATION_JSON)
                )
                .verifyComplete();
    }

    @Test
    void registrar_debeRetornar400_cuandoElBodyEsVacio() {
        // Preparar
        ServerRequest mockRequest = MockServerRequest.builder().body(Mono.empty());

        // Actuar
        Mono<ServerResponse> result = handler.registrar(mockRequest);

        // Verificar
        StepVerifier.create(result)
                .expectError();
    }
}