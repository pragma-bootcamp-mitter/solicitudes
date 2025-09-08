package co.com.pragma.bootcamp.usecase.token;

import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.model.token.Token;
import co.com.pragma.bootcamp.model.token.gateways.TokenGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import static co.com.pragma.bootcamp.model.exceptions.ApplicationErrors.UNAUTHORIZED_OPERATION;

@RequiredArgsConstructor
public class TokenUseCase {

    private final TokenGateway tokenGateway;

    public Mono<Token> authorize(String bearerToken) {
        return Mono.justOrEmpty(bearerToken)
                .filter(token -> token.startsWith("Bearer "))
                .switchIfEmpty(Mono.error(new BusinessException(UNAUTHORIZED_OPERATION)))
                .map(token -> token.substring(7))
                .flatMap(tokenGateway::validateToken)
                .onErrorResume(e -> Mono.error(new BusinessException(UNAUTHORIZED_OPERATION)));
    }
}