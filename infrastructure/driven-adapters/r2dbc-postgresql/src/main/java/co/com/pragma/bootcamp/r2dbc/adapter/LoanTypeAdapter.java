package co.com.pragma.bootcamp.r2dbc.adapter;

import co.com.pragma.bootcamp.model.loantype.LoanType;
import co.com.pragma.bootcamp.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.bootcamp.r2dbc.LoanTypeEntityRepository;
import co.com.pragma.bootcamp.r2dbc.mapper.LoanTypeMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class LoanTypeAdapter implements LoanTypeRepository {

    private final LoanTypeEntityRepository repository;
    private final LoanTypeMapper mapper;

    public LoanTypeAdapter(LoanTypeEntityRepository repository, LoanTypeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Mono<LoanType> findById(Integer id) {
        return repository.findByLoanTypeId(id)
                .map(mapper::toDomain);
    }
}