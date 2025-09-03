package co.com.pragma.bootcamp.r2dbc.adapter;

import co.com.pragma.bootcamp.model.applicationsummary.Pagination;
import co.com.pragma.bootcamp.r2dbc.ApplicationEntityRepository;
import co.com.pragma.bootcamp.r2dbc.entity.ApplicationEntity;
import co.com.pragma.bootcamp.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.bootcamp.r2dbc.mapper.ApplicationEntityMapper;
import org.springframework.stereotype.Repository;
import co.com.pragma.bootcamp.model.application.Application;
import co.com.pragma.bootcamp.model.application.gateways.ApplicationRepository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ApplicationRepositoryAdapter
        extends ReactiveAdapterOperations<Application, ApplicationEntity, String, ApplicationEntityRepository>
        implements ApplicationRepository {

    private final ApplicationEntityMapper applicationEntityMapper;
    private final TransactionalOperator transactionalOperator;

    public ApplicationRepositoryAdapter(ApplicationEntityRepository repository,
                                        ApplicationEntityMapper applicationEntityMapper,
                                        TransactionalOperator transactionalOperator) {
        super(repository, null, applicationEntityMapper::toDomain);
        this.applicationEntityMapper = applicationEntityMapper;
        this.repository = repository;
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<Application> save(Application application) {
        ApplicationEntity applicationEntityToSave = applicationEntityMapper.toEntity(application);
        return transactionalOperator.execute(transactionStatus ->
                        repository.save(applicationEntityToSave)
                )
                .single()
                .map(applicationEntityMapper::toDomain);
    }

    @Override
    public Flux<Application> findByClientDocument(String document) {
        return repository.findByClientDocument(document)
                .map(applicationEntityMapper::toDomain);
    }

    @Override
    public Flux<Application> findByStateIdAndPagination(Integer stateId, Pagination pagination) {
        return repository.findByStateId(stateId, pagination)
                .map(applicationEntityMapper::toDomain);
    }

    @Override
    public Flux<Application> findByClientDocumentAndStateId(String clientDocument, Integer stateId) {
        return repository.findByClientDocumentAndStateId(clientDocument, stateId)
                .map(applicationEntityMapper::toDomain);
    }

}

