package co.com.pragma.bootcamp.api.config;

import co.com.pragma.bootcamp.api.ApplicationHandler;
import co.com.pragma.bootcamp.api.ApplicationRouterRest;
import co.com.pragma.bootcamp.api.dto.ApplicationRequest;
import co.com.pragma.bootcamp.api.dto.ApplicationResponse;
import co.com.pragma.bootcamp.api.dto.ApiResponse;
import co.com.pragma.bootcamp.api.helper.ValidatorUtil;
import co.com.pragma.bootcamp.api.mapper.ApplicationMapper;
import co.com.pragma.bootcamp.security.config.SecurityConfig;
import co.com.pragma.bootcamp.usecase.listapplications.ListApplicationsUseCase;
import co.com.pragma.bootcamp.usecase.registerapplication.ApplicationUseCase;
import co.com.pragma.bootcamp.usecase.token.TokenUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerResponse;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {
        ApplicationRouterRest.class,
        SecurityConfig.class,
        ApplicationHandler.class,
        GlobalExceptionHandler.class,
        ValidatorUtil.class})
@WebFluxTest
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ApplicationUseCase useCase;

    @MockitoBean
    private ApplicationMapper mapper;

    @MockitoBean
    private ListApplicationsUseCase listUseCase;

    @MockitoBean
    private ApplicationHandler applicationHandler;

    @MockitoBean
    private TokenUseCase tokenUseCase;

    @MockitoBean
    private ReactiveAuthenticationManager authenticationManager;

    private static final String BASE_PATH = "/api/v1/applications";

    @Test
    @WithMockUser(roles = "CLIENT")
    void post_shouldRegisterApplication_andReturn201() {
        ApplicationResponse mockResponse = new ApplicationResponse();
        mockResponse.setId("1");

        when(applicationHandler.register(any())).thenReturn(
                ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ApiResponse.success(mockResponse))
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
                .jsonPath("$.data.id").isEqualTo("1");
    }

    @Test
    @WithMockUser(roles = "CLIENT")
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
    @WithMockUser(roles = "ADMIN")
    void get_shouldListApplications_andReturn200() {
        when(applicationHandler.list(any())).thenReturn(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ApiResponse.success(Collections.emptyList(), 0, 10, 0L, 0))
        );

        webTestClient.get()
                .uri(BASE_PATH)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @WithMockUser(roles = "USER")
    void post_shouldBeForbidden_forIncorrectRole() {
        webTestClient.post()
                .uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ApplicationRequest())
                .exchange()
                .expectStatus().isForbidden();
    }
}