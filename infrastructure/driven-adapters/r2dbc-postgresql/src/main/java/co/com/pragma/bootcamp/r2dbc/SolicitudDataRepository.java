package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.r2dbc.entity.SolicitudData;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface SolicitudDataRepository extends
        ReactiveCrudRepository<SolicitudData, String>,
        ReactiveQueryByExampleExecutor<SolicitudData> {
    Flux<SolicitudData> findByDocumentoCliente(String documentoCliente);
}
