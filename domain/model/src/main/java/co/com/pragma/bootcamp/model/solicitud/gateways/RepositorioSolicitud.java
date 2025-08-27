package co.com.pragma.bootcamp.model.solicitud.gateways;

import co.com.pragma.bootcamp.model.solicitud.Solicitud;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RepositorioSolicitud {
    Mono<Solicitud> save(Solicitud solicitud);
    Flux<Solicitud> findByDocumentoCliente(String documento);
    Flux<Solicitud> findAll();
}
