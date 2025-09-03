package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.config.GlobalExceptionHandler;
import co.com.pragma.bootcamp.api.dto.ApplicationRequest;
import co.com.pragma.bootcamp.api.helper.ValidatorUtil;
import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;

import static co.com.pragma.bootcamp.model.exceptions.ApplicationErrors.CLIENT_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ApplicationRouterRest.class, ApplicationHandler.class, ValidatorUtil.class, GlobalExceptionHandler.class})
@WebFluxTest
class ApplicationRouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ApplicationHandler applicationHandler;

    private static final String BASE_PATH = "/api/v1/applications";

    @Test
    void post_shouldBeRoutedToHandler() {
        when(applicationHandler.register(any(ServerRequest.class)))
                .thenReturn(ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(Map.of("id", "1")), Map.class));

        webTestClient.post()
                .uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo("1");
    }

    @Test
    void get_shouldBeRoutedToHandler() {
        when(applicationHandler.list(any(ServerRequest.class)))
                .thenReturn(ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(List.of(Map.of("id", "1"))));

        webTestClient.get()
                .uri(BASE_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("1");
    }


    @Test
    void get_listAllApplications_shouldReturnList() {
        when(applicationHandler.list(any(ServerRequest.class)))
                .thenReturn(ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(List.of(Map.of("id", "1"))), List.class));

        webTestClient.get()
                .uri(BASE_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("1");
    }
}
