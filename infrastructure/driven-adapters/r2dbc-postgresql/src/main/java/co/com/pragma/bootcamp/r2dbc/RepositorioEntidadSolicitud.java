package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.r2dbc.entidad.EntidadSolicitud;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface RepositorioEntidadSolicitud extends
        ReactiveCrudRepository<EntidadSolicitud, String>,
        ReactiveQueryByExampleExecutor<EntidadSolicitud> {
    Flux<EntidadSolicitud> findByDocumentoCliente(String documentoCliente);
}
