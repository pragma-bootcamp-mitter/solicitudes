package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.r2dbc.entity.TipoPrestamoData;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface TipoPrestamoDataRepository extends
        ReactiveCrudRepository<TipoPrestamoData, String>,
        ReactiveQueryByExampleExecutor<TipoPrestamoData> {
    Mono<TipoPrestamoData> findByNombre(String nombre);
}