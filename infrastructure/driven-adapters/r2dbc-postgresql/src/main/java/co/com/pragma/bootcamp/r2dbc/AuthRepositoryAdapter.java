package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.model.user.gateways.AuthRepository;
import co.com.pragma.bootcamp.r2dbc.mapper.AuthUserMapper;
import co.com.pragma.bootcamp.r2dbc.webclient.AuthClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthRepositoryAdapter implements AuthRepository {

    private final AuthClient client;
    private final AuthUserMapper mapper;

    @Override
    public Mono<User> getUserByDocument(String document) {
        return client.getUserByDocument(document)
                .map(mapper::toDomain);
    }
}
