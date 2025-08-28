package co.com.pragma.bootcamp.api.config;

import co.com.pragma.bootcamp.api.ApplicationHandler;
import co.com.pragma.bootcamp.api.ApplicationRouterRest;
import co.com.pragma.bootcamp.api.dto.ApplicationRequest;
import co.com.pragma.bootcamp.api.dto.ApplicationResponse;
import co.com.pragma.bootcamp.api.mapper.ApplicationMapper;
import co.com.pragma.bootcamp.model.application.gateways.ApplicationRepository;
import co.com.pragma.bootcamp.usecase.registrarsolicitud.RegisterApplicationUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import org.springframework.http.MediaType;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ApplicationRouterRest.class, ApplicationHandler.class})
@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class})
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private RegisterApplicationUseCase useCase;

    @MockitoBean
    private ApplicationMapper mapper;

    @MockitoBean
    private ApplicationHandler applicationHandler;

    @MockitoBean
    private ApplicationRepository applicationRepository;

    private static final String BASE_PATH = "/api/v1/applications";

    @Test
    void post_shouldRegisterApplication_andReturn201() {
        ApplicationResponse responseEsperada = ApplicationResponse.builder()
                .id("1")
                .build();

        when(applicationHandler.register(any())).thenReturn(
                ServerResponse.created(URI.create(BASE_PATH + "/1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(responseEsperada)
        );

        webTestClient.post()
                .uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ApplicationRequest("123456789", BigDecimal.valueOf(1000000),
                        12, "test@example.com",
                        new ApplicationRequest.StateRequest(),
                        new ApplicationRequest.LoanTypeRequest()))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo("1");
    }

    @Test
    void post_shouldReturnBadRequest_whenHandlerFails() {
        when(applicationHandler.register(any())).thenReturn(
                ServerResponse.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("error", "Invalid data"))
        );

        ApplicationRequest requestBody = new ApplicationRequest(
                "123456789",
                BigDecimal.valueOf(1000000),
                12,
                "test@example.com",
                new ApplicationRequest.StateRequest(),
                new ApplicationRequest.LoanTypeRequest()
        );

        webTestClient.post()
                .uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.error").isEqualTo("Invalid data");
    }

    @Test
    void securityHeaders_shouldBeConfiguredCorrectly() {
        when(applicationHandler.list(any())).thenReturn(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Flux.empty(), ApplicationResponse.class)
        );

        webTestClient.get()
                .uri(BASE_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Security-Policy", "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }
}