package co.com.pragma.bootcamp.r2dbc.webclient;

import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.r2dbc.entity.user.UserAuth;
import co.com.pragma.bootcamp.r2dbc.entity.user.UserAuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static co.com.pragma.bootcamp.model.exceptions.ApplicationErrors.CLIENT_NOT_FOUND;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthClient {

    private final WebClient authWebClient;

    public Mono<UserAuth> getUserByDocument(String document) {
        log.info("Consulting user by document: {}", document);
        return authWebClient.get()
                .uri("/api/v1/users/{document}", document)
                .exchangeToMono(response -> {
                    final HttpStatus statusCode = HttpStatus.valueOf(response.statusCode().value());

                    return switch (statusCode.series()) {
                        case SUCCESSFUL -> {
                            log.info("User found with document {}", document);
                            yield response.bodyToMono(UserAuthResponse.class)
                                    .map(UserAuthResponse::getData);
                        }
                        case CLIENT_ERROR -> {
                            log.warn("Client error when consulting user {}: {}", document, response.statusCode());
                            yield Mono.empty();
                        }
                        default -> {
                            log.error("Unexpected error when consulting user {}, status: {}", document, response.statusCode());
                            yield response.createException().flatMap(Mono::error);
                        }
                    };
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("WebClient error when consulting user {}: {}", document, ex.getMessage(), ex);
                    return Mono.error(new RuntimeException("Error when consulting Auth microservice"));
                });
    }
}