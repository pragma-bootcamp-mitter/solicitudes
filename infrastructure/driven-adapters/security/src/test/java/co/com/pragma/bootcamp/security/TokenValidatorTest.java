package co.com.pragma.bootcamp.security;

import co.com.pragma.bootcamp.security.jwt.TokenValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@ExtendWith(MockitoExtension.class)
class TokenValidatorTest {

    private TokenValidator tokenValidator;
    private static final String MOCK_SECRET = "thisisaverylongandsecuresecretkeyforjwttokenvalidator";
    private static final long MOCK_EXPIRATION_TIME = TimeUnit.HOURS.toMillis(1);
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_ROLE = "USER";

    private SecretKey mockSecretKey;

    @BeforeEach
    void setUp() {
        tokenValidator = new TokenValidator(MOCK_SECRET);
        mockSecretKey = Keys.hmacShaKeyFor(MOCK_SECRET.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void validateToken_shouldReturnClaims_whenTokenIsValid() {
        String validToken = Jwts.builder()
                .subject(TEST_EMAIL)
                .claim("role", TEST_ROLE)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + MOCK_EXPIRATION_TIME))
                .signWith(mockSecretKey)
                .compact();

        Mono<Claims> result = tokenValidator.validateToken(validToken);

        StepVerifier.create(result)
                .expectNextMatches(claims ->
                        claims.getSubject().equals(TEST_EMAIL) &&
                                claims.get("role", String.class).equals(TEST_ROLE)
                )
                .verifyComplete();
    }

    @Test
    void validateToken_shouldReturnEmptyMono_whenTokenIsExpired() {
        String expiredToken = Jwts.builder()
                .subject(TEST_EMAIL)
                .claim("role", TEST_ROLE)
                .issuedAt(new Date(System.currentTimeMillis() - 100000))
                .expiration(new Date(System.currentTimeMillis() - 50000))
                .signWith(mockSecretKey)
                .compact();

        Mono<Claims> result = tokenValidator.validateToken(expiredToken);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void validateToken_shouldReturnEmptyMono_whenTokenIsInvalid() {
        String invalidToken = "invalid.token.string";

        Mono<Claims> result = tokenValidator.validateToken(invalidToken);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void validateToken_shouldReturnEmptyMono_whenTokenHasInvalidSignature() {
        String differentSecret = "thisisadifferentsecretkeytorejectthetoken12345";
        SecretKey differentSecretKey = Keys.hmacShaKeyFor(differentSecret.getBytes(StandardCharsets.UTF_8));

        String tokenWithInvalidSignature = Jwts.builder()
                .subject(TEST_EMAIL)
                .claim("role", TEST_ROLE)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + MOCK_EXPIRATION_TIME))
                .signWith(differentSecretKey)
                .compact();

        Mono<Claims> result = tokenValidator.validateToken(tokenWithInvalidSignature);

        StepVerifier.create(result)
                .verifyComplete();
    }
}