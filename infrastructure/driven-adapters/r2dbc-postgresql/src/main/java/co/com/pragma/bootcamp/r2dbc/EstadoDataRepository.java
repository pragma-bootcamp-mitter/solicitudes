package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.r2dbc.entity.EstadoData;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface EstadoDataRepository extends
        ReactiveCrudRepository<EstadoData, String>,
        ReactiveQueryByExampleExecutor<EstadoData> {
    Mono<EstadoData> findByNombre(String nombre);
}