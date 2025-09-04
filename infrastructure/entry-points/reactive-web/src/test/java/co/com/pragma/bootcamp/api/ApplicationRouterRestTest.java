package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.config.GlobalExceptionHandler;
import co.com.pragma.bootcamp.api.dto.ApiResponse;
import co.com.pragma.bootcamp.api.dto.ApplicationSummaryResponse;
import co.com.pragma.bootcamp.api.helper.ValidatorUtil;
import co.com.pragma.bootcamp.model.token.Token;
import co.com.pragma.bootcamp.security.config.SecurityConfig;
import co.com.pragma.bootcamp.security.jwt.TokenValidator;
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
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {
        ApplicationRouterRest.class,
        ApplicationHandler.class,
        ValidatorUtil.class,
        GlobalExceptionHandler.class,
        SecurityConfig.class})
@WebFluxTest
class ApplicationRouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ApplicationHandler applicationHandler;

    @MockitoBean
    private ApplicationUseCase useCase;

    @MockitoBean
    private ListApplicationsUseCase listUseCase;

    @MockitoBean
    private ReactiveAuthenticationManager authenticationManager;

    @MockitoBean
    private TokenValidator tokenValidator;

    @MockitoBean
    private TokenUseCase tokenUseCase;

    @MockitoBean
    private Token token;


    private static final String BASE_PATH = "/api/v1/applications";

    @Test
    @WithMockUser(roles = "ADMIN")
    void registerApplication_shouldRouteToHandler_andReturn201() {
        when(applicationHandler.register(any(ServerRequest.class)))
                .thenReturn(ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("data", Map.of("id", "1"))));

        webTestClient.post()
                .uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.data.id").isEqualTo("1");
    }


    @Test
    @WithMockUser(roles = "ADVISOR")
    void listApplications_shouldRouteToHandler_andReturn200() {
        List<ApplicationSummaryResponse> mockList = Collections.singletonList(
                ApplicationSummaryResponse.builder()
                        .id("app-123")
                        .clientName("John Doe")
                        .stateName("PENDING_REVIEW")
                        .amount(BigDecimal.valueOf(1000))
                        .build()
        );

        when(applicationHandler.list(any(ServerRequest.class)))
                .thenReturn(ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ApiResponse.success(mockList, 0, 10, 100L,1)));

        webTestClient.get()
                .uri(BASE_PATH + "?page=0&size=10&stateName=PENDING_REVIEW")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.data[0].id").isEqualTo("app-123")
                .jsonPath("$.page").isEqualTo(0)
                .jsonPath("$.size").isEqualTo(10)
                .jsonPath("$.totalElements").isEqualTo(100);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void listApplications_shouldBeForbiddenForClientRole() {
        webTestClient.get()
                .uri(BASE_PATH)
                .exchange()
                .expectStatus().isForbidden();
    }
}