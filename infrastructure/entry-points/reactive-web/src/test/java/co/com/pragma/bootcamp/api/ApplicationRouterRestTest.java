package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.config.GlobalExceptionHandler;
import co.com.pragma.bootcamp.api.helper.ValidatorUtil;
import co.com.pragma.bootcamp.security.jwt.TokenValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import co.com.pragma.bootcamp.security.config.SecurityConfig;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
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
    private ReactiveAuthenticationManager authenticationManager;

    @MockitoBean
    private TokenValidator tokenValidator;

    private static final String BASE_PATH = "/api/v1/applications";

    @Test
    @WithMockUser(roles = "ADMIN")
    void post_shouldBeRoutedToHandler_and_return_201_with_user_role() {
        when(applicationHandler.register(any(ServerRequest.class)))
                .thenReturn(ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(Map.of("id", "1")), Map.class));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(Mono.just(new UsernamePasswordAuthenticationToken(
                        "user", null, AuthorityUtils.createAuthorityList("ROLE_ADMIN"))));

        when(tokenValidator.validateToken(any(String.class))).thenReturn(Mono.empty());

        webTestClient.post()
                .uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-client-jwt-token")
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo("1");
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void get_listAllApplications_shouldBeForbiddenForClientRole() {
        webTestClient.get()
                .uri(BASE_PATH)
                .exchange()
                .expectStatus().isForbidden();
    }
}
