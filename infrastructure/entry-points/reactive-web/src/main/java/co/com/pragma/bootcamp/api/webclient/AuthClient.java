package co.com.pragma.bootcamp.api.webclient;

import co.com.pragma.bootcamp.api.dto.UserAuth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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
                    if (response.statusCode().is2xxSuccessful()) {
                        log.info("User found with document {}", document);
                        return response.bodyToMono(UserAuth.class);
                    } else if (response.statusCode().value() == 404) {
                        log.warn("User not found with document {}", document);
                        return Mono.empty();
                    } else if (response.statusCode().is4xxClientError()) {
                        log.warn("Client error when consulting user {}: {}", document, response.statusCode());
                        return Mono.empty();
                    } else {
                        log.error("Unexpected error when consulting user {}, status: {}", document, response.statusCode());
                        return response.createException().flatMap(Mono::error);
                    }
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("WebClient error when consulting user {}: {}", document, ex.getMessage(), ex);
                    return Mono.error(new RuntimeException("Error when consulting Auth microservice"));
                });
    }
}