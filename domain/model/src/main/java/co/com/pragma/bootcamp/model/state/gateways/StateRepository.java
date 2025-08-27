package co.com.pragma.bootcamp.model.state.gateways;

import co.com.pragma.bootcamp.model.state.State;
import reactor.core.publisher.Mono;

public interface StateRepository  {
    Mono<State> findById(Integer id);
}
