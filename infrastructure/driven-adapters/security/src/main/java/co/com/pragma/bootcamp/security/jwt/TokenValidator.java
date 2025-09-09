package co.com.pragma.bootcamp.security.jwt;

import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.model.token.gateways.TokenGateway;
import co.com.pragma.bootcamp.model.token.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static co.com.pragma.bootcamp.model.exceptions.ApplicationErrors.UNAUTHORIZED_OPERATION;

@Component
public class TokenValidator implements TokenGateway {

    private final SecretKey secretKey;

    public TokenValidator(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Mono<Token> validateToken(String token) {
        return Mono.fromCallable(() -> Jwts.parser()
                        .verifyWith(this.secretKey)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload())
                .map(claims -> mapClaimsToToken(claims, token))
                .onErrorResume(e -> Mono.error(new BusinessException(UNAUTHORIZED_OPERATION)));
    }

    private Token mapClaimsToToken(Claims claims, String originalToken) {
        return Token.builder()
                .subject(claims.getSubject())
                .role(claims.get("role", String.class))
                .permissions(getPermissions(claims))
                .accessToken(originalToken)
                .build();
    }

    private List<String> getPermissions(Claims claims) {
        Object permissionsObject = claims.get("permissions");
        if (permissionsObject instanceof List) {
            return ((List<?>) permissionsObject).stream()
                    .map(Object::toString)
                    .toList();
        }
        return List.of();
    }
}