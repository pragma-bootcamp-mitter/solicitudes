package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.r2dbc.entidad.EntidadSolicitud;
import co.com.pragma.bootcamp.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.bootcamp.r2dbc.mapper.SolicitudMapper;
import org.springframework.stereotype.Repository;
import co.com.pragma.bootcamp.model.solicitud.Solicitud;
import co.com.pragma.bootcamp.model.solicitud.gateways.RepositorioSolicitud;
import reactor.core.publisher.Flux;

@Repository
public class RepositorioSolicitudAdapter
        extends ReactiveAdapterOperations<Solicitud, EntidadSolicitud, String, RepositorioEntidadSolicitud>
        implements RepositorioSolicitud {

    private final SolicitudMapper mapper;
    private final RepositorioEntidadSolicitud repository;

    public RepositorioSolicitudAdapter(RepositorioEntidadSolicitud repository, SolicitudMapper mapper) {
        super(repository, null, mapper::toDomain);
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    protected EntidadSolicitud toData(Solicitud entity) {
        return mapper.toData(entity);
    }

    @Override
    protected Solicitud toEntity(EntidadSolicitud data) {
        return mapper.toDomain(data);
    }

    @Override
    public Flux<Solicitud> findByDocumentoCliente(String documento) {
        return repository.findByDocumentoCliente(documento)
                .map(mapper::toDomain);
    }
}

