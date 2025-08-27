package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.model.loantype.LoanType;
import co.com.pragma.bootcamp.r2dbc.entity.LoanTypeEntity;
import co.com.pragma.bootcamp.r2dbc.mapper.LoanTypeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanTypeAdapterTest {

    @Mock
    private LoanTypeEntityRepository repository;

    @Mock
    private LoanTypeMapper mapper;

    @InjectMocks
    private LoanTypeAdapter adapter;

    private LoanTypeEntity loanTypeEntity;
    private LoanType loanType;

    @BeforeEach
    void setUp() {
        loanTypeEntity = LoanTypeEntity.builder()
                .loanTypeId(1)
                .name("Housing")
                .minimumAmount(BigDecimal.valueOf(1000000))
                .maximumAmount(BigDecimal.valueOf(500000000))
                .interestRate(BigDecimal.valueOf(0.05))
                .automaticValidation(true)
                .build();

        loanType = LoanType.builder()
                .id(1)
                .name("Housing")
                .minimumAmount(BigDecimal.valueOf(1000000))
                .maximumAmount(BigDecimal.valueOf(500000000))
                .interestRate(BigDecimal.valueOf(0.05))
                .automaticValidation(true)
                .build();
    }

    @Test
    void findById_shouldReturnLoanType_whenFound() {
        when(repository.findById("1"))
                .thenReturn(Mono.just(loanTypeEntity));
        when(mapper.toDomain(loanTypeEntity))
                .thenReturn(loanType);


        Mono<LoanType> result = adapter.findById(1);

        StepVerifier.create(result)
                .expectNext(loanType)
                .verifyComplete();

        verify(repository).findById("1");
        verify(mapper).toDomain(loanTypeEntity);
    }

    @Test
    void findById_shouldReturnEmptyMono_whenNotFound() {
        when(repository.findById("2"))
                .thenReturn(Mono.empty());


        Mono<LoanType> result = adapter.findById(2);

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();

        verify(repository).findById("2");
        verify(mapper, times(0)).toDomain(any());
    }

    @Test
    void findById_shouldPropagateError_whenRepositoryFails() {
        RuntimeException expectedException = new RuntimeException("Database error");
        when(repository.findById("3"))
                .thenReturn(Mono.error(expectedException));

        Mono<LoanType> result = adapter.findById(3);
        
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Database error"))
                .verify();

        verify(repository).findById("3");
        verify(mapper, times(0)).toDomain(any());
    }
}
