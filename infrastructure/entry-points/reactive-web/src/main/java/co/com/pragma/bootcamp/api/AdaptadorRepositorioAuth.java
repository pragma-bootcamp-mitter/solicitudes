package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.mapper.MapeadorAuthUsuario;
import co.com.pragma.bootcamp.api.webclient.AuthClient;
import co.com.pragma.bootcamp.model.user.Usuario;
import co.com.pragma.bootcamp.model.user.gateways.RepositorioAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AdaptadorRepositorioAuth implements RepositorioAuth {

    private final AuthClient client;
    private final MapeadorAuthUsuario mapper;

    @Override
    public Mono<Usuario> getUserByDocumento(String documento) {
        return client.getUserByDocumento(documento)
                .map(mapper::aDominio);
    }
}
