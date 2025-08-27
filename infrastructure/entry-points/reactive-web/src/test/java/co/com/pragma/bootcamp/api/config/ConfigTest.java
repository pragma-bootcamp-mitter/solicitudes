package co.com.pragma.bootcamp.api.config;

import co.com.pragma.bootcamp.api.Handler;
import co.com.pragma.bootcamp.api.RouterRest;
import co.com.pragma.bootcamp.api.dto.PeticionSolicitud;
import co.com.pragma.bootcamp.api.dto.RespuestaSolicitud;
import co.com.pragma.bootcamp.api.mapper.MapeadorSolicitud;
import co.com.pragma.bootcamp.model.solicitud.Solicitud;
import co.com.pragma.bootcamp.model.solicitud.gateways.RepositorioSolicitud;
import co.com.pragma.bootcamp.usecase.registrarsolicitud.RegistrarSolicitudCasoDeUso;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.http.MediaType;
import java.math.BigDecimal;
import java.net.URI;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, Handler.class})
@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class})
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private RegistrarSolicitudCasoDeUso useCase;

    @MockitoBean
    private MapeadorSolicitud mapper;

    @MockitoBean
    private Handler handler;

    @MockitoBean
    private RepositorioSolicitud repositorioSolicitud;

    private static final String BASE_PATH = "/api/v1/solicitud";

    @Test
    void post_debeRegistrarSolicitud_yRetornar201() {
        RespuestaSolicitud responseEsperada = RespuestaSolicitud.builder()
                .id("1")
                .build();

        when(handler.registrar(any())).thenReturn(
                ServerResponse.created(URI.create(BASE_PATH + "/1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(responseEsperada)
        );

        webTestClient.post()
                .uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new PeticionSolicitud("123456789", BigDecimal.valueOf(1000000),
                        12, "test@example.com",
                        new PeticionSolicitud.EstadoRequest(),
                        new PeticionSolicitud.TipoPrestamoRequest()))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo("1");
    }

    //@Test
    void post_debeRetornarBadRequest_cuandoElUseCaseFalla() {
        PeticionSolicitud requestBody = new PeticionSolicitud(
                "123456789",
                BigDecimal.valueOf(1000000),
                12,
                "test@example.com",
                new PeticionSolicitud.EstadoRequest(),
                new PeticionSolicitud.TipoPrestamoRequest()
        );

        when(mapper.aDominio(any(PeticionSolicitud.class)))
                .thenReturn(Solicitud.builder()
                        .documentoCliente("123456789")
                        .monto(BigDecimal.valueOf(1000000))
                        .build());

        when(useCase.registrar(any(Solicitud.class)))
                .thenReturn(Mono.error(new IllegalArgumentException("Datos inválidos")));

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

    //@Test
    void losEncabezadosDeSeguridad_debenEstarConfiguradosCorrectamente() {
        when(repositorioSolicitud.findAll()).thenReturn(Flux.empty());

        webTestClient.get()
                .uri(BASE_PATH)
                .exchange()
                .expectStatus().isOk() // Now the GET request should be OK and not a 404
                .expectHeader().valueEquals("Content-Security-Policy", "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }
}