package co.com.pragma.bootcamp.security.config;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static co.com.pragma.bootcamp.security.util.AuthConstants.TOKEN_KEY;

@Component
public class TokenContextFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (token != null && token.startsWith("Bearer ")) {
            String cleanToken = token.substring(7);
            return chain.filter(exchange)
                    .contextWrite(context -> context.put(TOKEN_KEY, cleanToken));
        }
        return chain.filter(exchange);
    }
}