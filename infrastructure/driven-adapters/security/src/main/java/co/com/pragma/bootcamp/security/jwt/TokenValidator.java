package co.com.pragma.bootcamp.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class TokenValidator {

    private final SecretKey secretKey;

    public TokenValidator(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Mono<Claims> validateToken(String token) {
        return Mono.fromCallable(() -> Jwts.parser()
                        .verifyWith(this.secretKey)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload())
                .onErrorResume(e -> Mono.empty());
    }
}