package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.r2dbc.entidad.EntidadEstado;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RepositorioEntidadEstado extends
        ReactiveCrudRepository<EntidadEstado, String>,
        ReactiveQueryByExampleExecutor<EntidadEstado> {
    Mono<EntidadEstado> findByNombre(String nombre);
}