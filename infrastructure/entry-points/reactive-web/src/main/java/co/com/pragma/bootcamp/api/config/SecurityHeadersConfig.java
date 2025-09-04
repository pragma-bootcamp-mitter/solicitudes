package co.com.pragma.bootcamp.api.config;

import co.com.pragma.bootcamp.usecase.token.TokenUseCase;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import org.springframework.http.server.reactive.ServerHttpRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SecurityHeadersConfig implements WebFilter {

    private final TokenUseCase tokenUseCase;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        HttpHeaders headers = exchange.getResponse().getHeaders();
        headers.set("Content-Security-Policy", "default-src 'self'; frame-ancestors 'self'; form-action 'self'");
        headers.set("Strict-Transport-Security", "max-age=31536000;");
        headers.set("X-Content-Type-Options", "nosniff");
        headers.set("Server", "");
        headers.set("Cache-Control", "no-store");
        headers.set("Pragma", "no-cache");
        headers.set("Referrer-Policy", "strict-origin-when-cross-origin");

        ServerHttpRequest request = exchange.getRequest();
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        return tokenUseCase.authorize(bearerToken)
                .flatMap(token -> chain.filter(exchange)
                        .contextWrite(ctx -> ctx.put("token", token))).switchIfEmpty(
                        chain.filter(exchange)
                );
    }
}