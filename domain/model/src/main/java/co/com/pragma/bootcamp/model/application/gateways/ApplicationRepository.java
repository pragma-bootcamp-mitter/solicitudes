package co.com.pragma.bootcamp.model.application.gateways;

import co.com.pragma.bootcamp.model.application.Application;
import co.com.pragma.bootcamp.model.applicationsummary.PageModel;
import co.com.pragma.bootcamp.model.applicationsummary.Pagination;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ApplicationRepository {
    Mono<Application> save(Application application);
    Flux<Application> findByClientDocument(String document);
    Mono<PageModel<Application>> findByStateIdAndPagination(Pagination pagination, Integer stateId);
    Flux<Application> findByClientDocumentAndStateId(String clientDocument, Integer stateId);
    Mono<Long> countByStateId(Integer stateId);
}
