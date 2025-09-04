package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.r2dbc.entity.ApplicationEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ApplicationEntityRepository extends
        ReactiveCrudRepository<ApplicationEntity, String>,
        ReactiveQueryByExampleExecutor<ApplicationEntity> {
    Flux<ApplicationEntity> findByClientDocument(String clientDocument);
    Flux<ApplicationEntity> findByStateId(Integer stateId, Pageable pageable);
    Flux<ApplicationEntity> findByClientDocumentAndStateId(String clientDocument, Integer stateId);
    Mono<Long> countByStateId(Integer stateId);
}
