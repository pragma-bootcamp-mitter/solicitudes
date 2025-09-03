package co.com.pragma.bootcamp.model.loantype.gateways;

import co.com.pragma.bootcamp.model.loantype.LoanType;
import reactor.core.publisher.Mono;

public interface LoanTypeRepository  {
    Mono<LoanType> findById(Integer id);
}
