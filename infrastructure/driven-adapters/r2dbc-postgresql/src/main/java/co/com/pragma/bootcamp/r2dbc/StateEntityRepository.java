package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.r2dbc.entity.StateEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface StateEntityRepository extends
        ReactiveCrudRepository<StateEntity, String>,
        ReactiveQueryByExampleExecutor<StateEntity> {
    Mono<StateEntity> findByName(String nombre);
}