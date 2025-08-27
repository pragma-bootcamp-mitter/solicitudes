package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.r2dbc.entidad.EntidadTipoPrestamo;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RepositorioEntidadTipoPrestamo extends
        ReactiveCrudRepository<EntidadTipoPrestamo, String>,
        ReactiveQueryByExampleExecutor<EntidadTipoPrestamo> {
    Mono<EntidadTipoPrestamo> findByNombre(String nombre);
}