package co.com.pragma.bootcamp.model.estado.gateways;

import co.com.pragma.bootcamp.model.estado.Estado;
import reactor.core.publisher.Mono;

public interface RepositorioEstado {
    Mono<Estado> findById(Integer id);
}
