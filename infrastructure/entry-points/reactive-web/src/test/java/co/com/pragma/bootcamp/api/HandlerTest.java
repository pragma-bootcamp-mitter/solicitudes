package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.dto.PeticionSolicitud;
import co.com.pragma.bootcamp.api.dto.RespuestaSolicitud;
import co.com.pragma.bootcamp.api.mapper.MapeadorSolicitud;
import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.model.solicitud.Solicitud;
import co.com.pragma.bootcamp.model.solicitud.gateways.RepositorioSolicitud;
import co.com.pragma.bootcamp.usecase.registrarsolicitud.RegistrarSolicitudCasoDeUso;
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
    private RegistrarSolicitudCasoDeUso useCase;

    @Mock
    private MapeadorSolicitud mapper;

    @Mock
    private RepositorioSolicitud repositorioSolicitud;

    @Mock
    private Validator validator;

    @InjectMocks
    private Handler handler;

    private PeticionSolicitud request;
    private Solicitud domain;
    private Solicitud saved;
    private RespuestaSolicitud response;

    @BeforeEach
    void setUp() {
        request = new PeticionSolicitud(
                "123456789",
                BigDecimal.valueOf(1000000),
                12,
                "test@example.com",
                new PeticionSolicitud.EstadoRequest(),
                new PeticionSolicitud.TipoPrestamoRequest()
        );
        request.getEstado().setId(1);
        request.getTipoPrestamo().setId(1);

        domain = Solicitud.builder().id("id-domain").build();
        saved = Solicitud.builder().id("id-saved").build();
        response = RespuestaSolicitud.builder().id("id-response").build();
    }

    @Test
    void registrar_debeRetornar201_cuandoEsExitoso() {
        when(mapper.aDominio(any(PeticionSolicitud.class))).thenReturn(domain);
        when(useCase.registrar(any(Solicitud.class))).thenReturn(Mono.just(saved));
        when(mapper.aRepuesta(any(Solicitud.class))).thenReturn(response);

        ServerRequest mockRequest = MockServerRequest.builder()
                .body(Mono.just(request));

        Mono<ServerResponse> result = handler.registrar(mockRequest);

        StepVerifier.create(result)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode().equals(HttpStatus.CREATED) &&
                                serverResponse.headers().getContentType().equals(MediaType.APPLICATION_JSON)
                )
                .verifyComplete();
    }

    @Test
    void registrar_debeRetornar409_cuandoElUseCaseLanzaBusinessException() {
        when(mapper.aDominio(any(PeticionSolicitud.class))).thenReturn(domain);
        when(useCase.registrar(any(Solicitud.class)))
                .thenReturn(Mono.error(new BusinessException("Solicitud duplicada")));

        ServerRequest mockRequest = MockServerRequest.builder()
                .body(Mono.just(request));

        Mono<ServerResponse> result = handler.registrar(mockRequest);

        StepVerifier.create(result)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode().equals(HttpStatus.CONFLICT) &&
                                serverResponse.headers().getContentType().equals(MediaType.APPLICATION_JSON)
                )
                .verifyComplete();
    }

    @Test
    void registrar_debeRetornar500_cuandoElMapeoFalla() {
        when(mapper.aDominio(any(PeticionSolicitud.class)))
                .thenThrow(new RuntimeException("Error de mapeo"));

        ServerRequest mockRequest = MockServerRequest.builder()
                .body(Mono.just(request));

        Mono<ServerResponse> result = handler.registrar(mockRequest);

        StepVerifier.create(result)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR) &&
                                serverResponse.headers().getContentType().equals(MediaType.APPLICATION_JSON)
                )
                .verifyComplete();
    }

    @Test
    void registrar_debeRetornar400_cuandoElBodyEsVacio() {
        ServerRequest mockRequest = MockServerRequest.builder().body(Mono.empty());

        Mono<ServerResponse> result = handler.registrar(mockRequest);

        StepVerifier.create(result)
                .expectError();
    }


    @Test
    void listar_debeRetornar200_cuandoExitoso() {
        when(repositorioSolicitud.findAll()).thenReturn(Flux.just(domain));
        when(mapper.aRepuesta(domain)).thenReturn(response);

        ServerRequest mockRequest = MockServerRequest.builder().build();

        Mono<ServerResponse> result = handler.listar(mockRequest);

        StepVerifier.create(result)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode().is2xxSuccessful() &&
                                serverResponse.headers().getContentType().equals(MediaType.APPLICATION_JSON)
                )
                .verifyComplete();
    }

    @Test
    void listar_debeRetornar200_conListaVacia() {
        when(repositorioSolicitud.findAll()).thenReturn(Flux.empty());

        ServerRequest mockRequest = MockServerRequest.builder().build();

        Mono<ServerResponse> result = handler.listar(mockRequest);

        StepVerifier.create(result)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode().is2xxSuccessful()
                )
                .verifyComplete();
    }

    @Test
    void listar_debeRetornar500_cuandoOcurreError() {
        when(repositorioSolicitud.findAll()).thenReturn(Flux.error(new RuntimeException("DB error")));

        ServerRequest mockRequest = MockServerRequest.builder().build();

        Mono<ServerResponse> result = handler.listar(mockRequest);

        StepVerifier.create(result)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode().is5xxServerError() &&
                                serverResponse.headers().getContentType().equals(MediaType.APPLICATION_JSON)
                )
                .verifyComplete();
    }
}