package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.mapper.AuthUserMapper;
import co.com.pragma.bootcamp.api.webclient.AuthClient;
import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.model.user.gateways.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthRepositoryAdapter implements AuthRepository {

    private final AuthClient client;
    private final AuthUserMapper mapper;

    @Override
    public Mono<User> getUserByDocumento(String documento) {
        return client.getUserByDocumento(documento)
                .map(mapper::toDomain);
    }
}
