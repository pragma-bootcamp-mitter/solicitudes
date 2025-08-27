package co.com.pragma.bootcamp.model.user.gateways;

import co.com.pragma.bootcamp.model.user.Usuario;
import reactor.core.publisher.Mono;

public interface RepositorioAuth {
    Mono<Usuario> getUserByDocumento(String documento);
}