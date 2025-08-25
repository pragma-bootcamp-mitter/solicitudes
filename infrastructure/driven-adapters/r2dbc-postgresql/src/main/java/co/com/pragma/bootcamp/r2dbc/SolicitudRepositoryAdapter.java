package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.bootcamp.r2dbc.mapper.SolicitudMapper;
import org.springframework.stereotype.Repository;
import co.com.pragma.bootcamp.model.solicitud.Solicitud;
import co.com.pragma.bootcamp.model.solicitud.gateways.SolicitudRepository;
import co.com.pragma.bootcamp.r2dbc.entity.SolicitudData;
import reactor.core.publisher.Flux;

@Repository
public class SolicitudRepositoryAdapter
        extends ReactiveAdapterOperations<Solicitud, SolicitudData, String, SolicitudDataRepository>
        implements SolicitudRepository {

    private final SolicitudMapper mapper;
    private final SolicitudDataRepository repository;

    public SolicitudRepositoryAdapter(SolicitudDataRepository repository, SolicitudMapper mapper) {
        super(repository, null, mapper::toDomain);
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    protected SolicitudData toData(Solicitud entity) {
        return mapper.toData(entity);
    }

    @Override
    protected Solicitud toEntity(SolicitudData data) {
        return mapper.toDomain(data);
    }

    @Override
    public Flux<Solicitud> findByDocumentoCliente(String documento) {
        return repository.findByDocumentoCliente(documento)
                .map(mapper::toDomain);
    }
}

