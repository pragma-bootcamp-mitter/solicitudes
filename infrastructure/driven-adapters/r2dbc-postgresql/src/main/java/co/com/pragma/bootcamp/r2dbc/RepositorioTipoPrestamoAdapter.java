package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.model.tipoprestamo.TipoPrestamo;
import co.com.pragma.bootcamp.model.tipoprestamo.gateways.RepositorioTipoPrestamo;
import co.com.pragma.bootcamp.r2dbc.mapper.TipoPrestamoMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class RepositorioTipoPrestamoAdapter implements RepositorioTipoPrestamo {

    private final RepositorioEntidadTipoPrestamo repository;
    private final TipoPrestamoMapper mapper;

    public RepositorioTipoPrestamoAdapter(RepositorioEntidadTipoPrestamo repository, TipoPrestamoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Mono<TipoPrestamo> findById(Integer id) {
        return repository.findById(String.valueOf(id))
                .map(mapper::toDomain);
    }
}
