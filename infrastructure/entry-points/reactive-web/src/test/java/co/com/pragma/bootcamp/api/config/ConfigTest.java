package co.com.pragma.bootcamp.api.config;

import co.com.pragma.bootcamp.api.Handler;
import co.com.pragma.bootcamp.api.RouterRest;
import co.com.pragma.bootcamp.api.dto.SolicitudRequest;
import co.com.pragma.bootcamp.api.dto.SolicitudResponse;
import co.com.pragma.bootcamp.api.mapper.SolicitudDtoMapper;
import co.com.pragma.bootcamp.model.solicitud.Solicitud;
import co.com.pragma.bootcamp.usecase.registrarsolicitud.RegistrarSolicitudUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import org.springframework.http.MediaType;
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, Handler.class})
@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class})
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private RegistrarSolicitudUseCase useCase;

    @MockitoBean
    private SolicitudDtoMapper mapper;

    private static final String BASE_PATH = "/api/v1/solicitud";

    @Test
    void post_debeRegistrarSolicitud_yRetornar201() {
        SolicitudRequest requestBody = new SolicitudRequest(
                "123456789",
                BigDecimal.valueOf(1000000),
                12,
                "test@example.com",
                new SolicitudRequest.EstadoRequest(),
                new SolicitudRequest.TipoPrestamoRequest()
        );

        Solicitud solicitudDomain = Solicitud.builder()
                .documentoCliente("123456789")
                .monto(BigDecimal.valueOf(1000000))
                .build();

        Solicitud solicitudGuardada = Solicitud.builder()
                .id("1")
                .documentoCliente("123456789")
                .monto(BigDecimal.valueOf(1000000))
                .build();

        SolicitudResponse responseEsperada = SolicitudResponse.builder()
                .id("1")
                .build();

        when(mapper.toDomain(any(SolicitudRequest.class))).thenReturn(solicitudDomain);
        when(useCase.registrar(any(Solicitud.class))).thenReturn(Mono.just(solicitudGuardada));
        when(mapper.toResponse(any(Solicitud.class))).thenReturn(responseEsperada);

        webTestClient.post()
                .uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo("1");
    }

    @Test
    void post_debeRetornarBadRequest_cuandoElUseCaseFalla() {
        SolicitudRequest requestBody = new SolicitudRequest(
                "123456789",
                BigDecimal.valueOf(1000000),
                12,
                "test@example.com",
                new SolicitudRequest.EstadoRequest(),
                new SolicitudRequest.TipoPrestamoRequest()
        );

        when(useCase.registrar(any(Solicitud.class)))
                .thenReturn(Mono.error(new IllegalArgumentException("Datos inválidos")));

        when(mapper.toDomain(any(SolicitudRequest.class)))
                .thenReturn(Solicitud.builder()
                        .documentoCliente("123456789")
                        .monto(BigDecimal.valueOf(1000000))
                        .build());

        webTestClient.post()
                .uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.error").isEqualTo("Datos inválidos");
    }

    @Test
    void losEncabezadosDeSeguridad_debenEstarConfiguradosCorrectamente() {
        webTestClient.get()
                .uri(BASE_PATH)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().valueEquals("Content-Security-Policy", "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }
}