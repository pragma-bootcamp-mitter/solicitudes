package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.model.tipoprestamo.TipoPrestamo;
import co.com.pragma.bootcamp.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.pragma.bootcamp.r2dbc.mapper.TipoPrestamoMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class TipoPrestamoRepositoryAdapter implements TipoPrestamoRepository {

    private final TipoPrestamoDataRepository repository;
    private final TipoPrestamoMapper mapper;

    public TipoPrestamoRepositoryAdapter(TipoPrestamoDataRepository repository, TipoPrestamoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Mono<TipoPrestamo> findById(Integer id) {
        return repository.findById(String.valueOf(id))
                .map(mapper::toDomain);
    }
}
