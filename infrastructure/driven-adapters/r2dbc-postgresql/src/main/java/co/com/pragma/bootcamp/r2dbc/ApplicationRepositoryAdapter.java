package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.r2dbc.entity.ApplicationEntity;
import co.com.pragma.bootcamp.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.bootcamp.r2dbc.mapper.ApplicationEntityMapper;
import org.springframework.stereotype.Repository;
import co.com.pragma.bootcamp.model.application.Application;
import co.com.pragma.bootcamp.model.application.gateways.ApplicationRepository;
import reactor.core.publisher.Flux;

@Repository
public class ApplicationRepositoryAdapter
        extends ReactiveAdapterOperations<Application, ApplicationEntity, String, ApplicationEntityRepository>
        implements ApplicationRepository {

    private final ApplicationEntityMapper mapper;
    private final ApplicationEntityRepository repository;

    public ApplicationRepositoryAdapter(ApplicationEntityRepository repository, ApplicationEntityMapper mapper) {
        super(repository, null, mapper::toDomain);
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    protected ApplicationEntity toData(Application entity) {
        return mapper.toEntity(entity);
    }

    @Override
    protected Application toEntity(ApplicationEntity data) {
        return mapper.toDomain(data);
    }

    @Override
    public Flux<Application> findByClientDocument(String document) {
        return repository.findByClientDocument(document)
                .map(mapper::toDomain);
    }
}

