package co.com.pragma.bootcamp.r2dbc.adapter;

import co.com.pragma.bootcamp.model.state.State;
import co.com.pragma.bootcamp.model.state.gateways.StateRepository;
import co.com.pragma.bootcamp.r2dbc.StateEntityRepository;
import co.com.pragma.bootcamp.r2dbc.mapper.StateMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class StateAdapter implements StateRepository {

    private final StateEntityRepository repository;
    private final StateMapper mapper;

    public StateAdapter(StateEntityRepository repository, StateMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Mono<State> findById(Integer id) {
        return repository.findByStateId(id)
                .map(mapper::toDomain);
    }
}