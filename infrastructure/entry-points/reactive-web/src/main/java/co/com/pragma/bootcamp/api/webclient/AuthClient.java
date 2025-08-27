package co.com.pragma.bootcamp.api.webclient;

import co.com.pragma.bootcamp.api.dto.UsuarioAuth;
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

    public Mono<UsuarioAuth> getUserByDocumento(String documento) {
        log.info("Consultando usuario por documento: {}", documento);

        return authWebClient.get()
                .uri("/api/v1/usuarios/{documento}", documento)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        log.info("Usuario encontrado con documento {}", documento);
                        return response.bodyToMono(UsuarioAuth.class);
                    } else if (response.statusCode().value() == 404) {
                        log.warn("Usuario no encontrado con documento {}", documento);
                        return Mono.empty();
                    } else if (response.statusCode().is4xxClientError()) {
                        log.warn("Error de cliente al consultar usuario {}: {}", documento, response.statusCode());
                        return Mono.empty();
                    } else {
                        log.error("Error inesperado consultando usuario {}, status: {}", documento, response.statusCode());
                        return response.createException().flatMap(Mono::error);
                    }
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("WebClient error al consultar usuario {}: {}", documento, ex.getMessage(), ex);
                    return Mono.error(new RuntimeException("Error al consultar microservicio Auth"));
                });
    }
}