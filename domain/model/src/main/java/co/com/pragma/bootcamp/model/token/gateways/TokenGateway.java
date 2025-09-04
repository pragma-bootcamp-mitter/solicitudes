package co.com.pragma.bootcamp.model.token.gateways;

import co.com.pragma.bootcamp.model.token.Token;
import reactor.core.publisher.Mono;

public interface TokenGateway {

    Mono<Token> validateToken(String token);
}
