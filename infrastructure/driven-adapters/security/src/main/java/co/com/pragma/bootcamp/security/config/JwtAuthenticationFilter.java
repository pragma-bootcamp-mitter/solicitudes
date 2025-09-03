package co.com.pragma.bootcamp.security.config;

import co.com.pragma.bootcamp.security.jwt.TokenValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class JwtAuthenticationFilter extends AuthenticationWebFilter {

    private static final String ROLE_PREFIX = "ROLE_";
    private static final String BEARER_PREFIX = "Bearer ";

    private final TokenValidator tokenValidator;

    public JwtAuthenticationFilter(ReactiveAuthenticationManager authenticationManager, TokenValidator tokenValidator) {
        super(authenticationManager);
        this.tokenValidator = tokenValidator;
        setServerAuthenticationConverter(createAuthenticationConverter());
    }

    private ServerAuthenticationConverter createAuthenticationConverter() {
        return exchange -> Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .filter(authHeader -> authHeader.startsWith(BEARER_PREFIX))
                .map(authHeader -> authHeader.substring(BEARER_PREFIX.length()))
                .flatMap(this::convertTokenToAuthentication)
                .doOnError(e -> log.error("Error during authentication conversion: {}", e.getMessage()));
    }

    private Mono<Authentication> convertTokenToAuthentication(String authToken) {
        return tokenValidator.validateToken(authToken)
                .map(claims -> {
                    //aqui cambie el email por el document
                    String document = claims.getSubject();
                    String role = claims.get("role", String.class);
                    //String email = claims.get("email", String.class);
                    List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(ROLE_PREFIX + role));
                    return new UsernamePasswordAuthenticationToken(document, null, authorities);
                });
    }
}