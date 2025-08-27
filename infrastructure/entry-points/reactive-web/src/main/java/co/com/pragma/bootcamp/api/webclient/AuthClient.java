package co.com.pragma.bootcamp.api.webclient;

import co.com.pragma.bootcamp.api.dto.UsuarioAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthClient {

    private final WebClient authWebClient;

    public Mono<UsuarioAuth> getUserByDocumento(String documento) {
        return authWebClient.get()
                .uri("/api/v1/usuarios/{documento}", documento)
                .retrieve()
                .onStatus(status -> status.value() == 404, resp -> Mono.empty())
                .bodyToMono(UsuarioAuth.class);
    }
}
