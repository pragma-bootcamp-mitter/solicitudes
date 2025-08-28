package co.com.pragma.bootcamp.model.user.gateways;

import co.com.pragma.bootcamp.model.user.User;
import reactor.core.publisher.Mono;

public interface AuthRepository {
    Mono<User> getUserByDocument(String document);
}