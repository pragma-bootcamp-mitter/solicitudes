package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.r2dbc.entity.ApplicationEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ApplicationEntityRepository extends
        ReactiveCrudRepository<ApplicationEntity, String>,
        ReactiveQueryByExampleExecutor<ApplicationEntity> {
    Flux<ApplicationEntity> findByClientDocument(String clientDocument);
}
