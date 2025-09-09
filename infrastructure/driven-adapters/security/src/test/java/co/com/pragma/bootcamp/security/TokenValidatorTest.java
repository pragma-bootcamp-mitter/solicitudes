package co.com.pragma.bootcamp.security;

import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.model.token.Token;
import co.com.pragma.bootcamp.security.jwt.TokenValidator;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

import static co.com.pragma.bootcamp.model.exceptions.ApplicationErrors.UNAUTHORIZED_OPERATION;

@ExtendWith(MockitoExtension.class)
class TokenValidatorTest {

    private TokenValidator tokenValidator;
    private static final String MOCK_SECRET = "thisisaverylongandsecuresecretkeyforjwttokenvalidator";
    private static final long MOCK_EXPIRATION_TIME = TimeUnit.HOURS.toMillis(1);
    private static final String TEST_SUBJECT = "test@example.com";
    private static final String TEST_ROLE = "USER";
    private static final List<String> TEST_PERMISSIONS = List.of("READ", "WRITE");

    private SecretKey mockSecretKey;

    @BeforeEach
    void setUp() {
        tokenValidator = new TokenValidator(MOCK_SECRET);
        mockSecretKey = Keys.hmacShaKeyFor(MOCK_SECRET.getBytes(StandardCharsets.UTF_8));
    }



    @Test
    void validateToken_shouldReturnToken_whenTokenIsValid() {
        String validToken = Jwts.builder()
                .subject(TEST_SUBJECT)
                .claim("role", TEST_ROLE)
                .claim("permissions", TEST_PERMISSIONS)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + MOCK_EXPIRATION_TIME))
                .signWith(mockSecretKey)
                .compact();

        Mono<Token> result = tokenValidator.validateToken(validToken);

        StepVerifier.create(result)
                .expectNextMatches(token ->
                        token.getSubject().equals(TEST_SUBJECT) &&
                                token.getRole().equals(TEST_ROLE) &&
                                token.getPermissions().equals(TEST_PERMISSIONS) &&
                                token.getAccessToken().equals(validToken)
                )
                .verifyComplete();
    }



    @Test
    void validateToken_shouldThrowException_whenTokenIsExpired() {
        String expiredToken = Jwts.builder()
                .subject(TEST_SUBJECT)
                .claim("role", TEST_ROLE)
                .issuedAt(new Date(System.currentTimeMillis() - 100000))
                .expiration(new Date(System.currentTimeMillis() - 50000))
                .signWith(mockSecretKey)
                .compact();

        Mono<Token> result = tokenValidator.validateToken(expiredToken);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                                ((BusinessException) throwable).getApplicationError().equals(UNAUTHORIZED_OPERATION)
                )
                .verify();
    }

    @Test
    void validateToken_shouldThrowException_whenTokenIsInvalid() {
        String invalidToken = "invalid.token.string";

        Mono<Token> result = tokenValidator.validateToken(invalidToken);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                                ((BusinessException) throwable).getApplicationError().equals(UNAUTHORIZED_OPERATION)
                )
                .verify();
    }

    @Test
    void validateToken_shouldThrowException_whenTokenHasInvalidSignature() {
        String differentSecret = "thisisadifferentsecretkeytorejectthetoken12345";
        SecretKey differentSecretKey = Keys.hmacShaKeyFor(differentSecret.getBytes(StandardCharsets.UTF_8));

        String tokenWithInvalidSignature = Jwts.builder()
                .subject(TEST_SUBJECT)
                .claim("role", TEST_ROLE)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + MOCK_EXPIRATION_TIME))
                .signWith(differentSecretKey)
                .compact();

        Mono<Token> result = tokenValidator.validateToken(tokenWithInvalidSignature);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                                ((BusinessException) throwable).getApplicationError().equals(UNAUTHORIZED_OPERATION)
                )
                .verify();
    }
}