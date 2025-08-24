package co.com.pragma.bootcamp.model.tipoprestamo.gateways;

import co.com.pragma.bootcamp.model.tipoprestamo.TipoPrestamo;
import reactor.core.publisher.Mono;

public interface TipoPrestamoRepository {
    Mono<TipoPrestamo> findById(Integer id);
}
