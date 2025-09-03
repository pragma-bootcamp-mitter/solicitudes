package co.com.pragma.bootcamp.model.application.gateways;

import co.com.pragma.bootcamp.model.application.Application;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ApplicationRepository {
    Mono<Application> save(Application application);
    Flux<Application> findByClientDocument(String document);
    Flux<Application> findAll();
}
