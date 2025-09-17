package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.model.state.State;
import co.com.pragma.bootcamp.model.application.Application;
import co.com.pragma.bootcamp.model.loantype.LoanType;
import co.com.pragma.bootcamp.r2dbc.adapter.ApplicationRepositoryAdapter;
import co.com.pragma.bootcamp.r2dbc.entity.ApplicationEntity;
import co.com.pragma.bootcamp.r2dbc.mapper.ApplicationEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.ReactiveTransaction;
import org.springframework.transaction.reactive.TransactionCallback;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationRepositoryAdapterTest {

    @Mock
    private ApplicationEntityRepository repository;

    @Mock
    private ApplicationEntityMapper mapper;

    @Mock
    private TransactionalOperator transactionalOperator;

    @InjectMocks
    private ApplicationRepositoryAdapter adapter;

    private Application application;
    private ApplicationEntity applicationEntity;

    @BeforeEach
    void setup() {
        application = Application.builder()
                .id("1")
                .clientDocument("123456789")
                .amount(BigDecimal.valueOf(1000000))
                .termMonths(12)
                .email("test@example.com")
                .stateId(1)
                .loanTypeId(1)
                .build();

        applicationEntity = new ApplicationEntity(
                "1",
                "123456789",
                BigDecimal.valueOf(1000000),
                12,
                "test@example.com",
                1,
                1
        );
    }

    @Test
    void findByClientDocument_shouldReturnApplications_whenFound() {
        when(repository.findByClientDocument("123456789"))
                .thenReturn(Flux.just(applicationEntity, applicationEntity));
        when(mapper.toDomain(applicationEntity)).thenReturn(application);
        when(mapper.toDomain(applicationEntity)).thenReturn(application);

        Flux<Application> result = adapter.findByClientDocument("123456789");

        StepVerifier.create(result)
                .expectNext(application, application)
                .verifyComplete();

        verify(repository).findByClientDocument("123456789");
        verify(mapper, times(2)).toDomain(any(ApplicationEntity.class));
    }

    @Test
    void findByClientDocument_shouldReturnEmptyMono_whenNoApplicationsFound() {
        when(repository.findByClientDocument("987654321"))
                .thenReturn(Flux.empty());

        Flux<Application> result = adapter.findByClientDocument("987654321");

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();

        verify(repository).findByClientDocument("987654321");
        verify(mapper, times(0)).toDomain(any(ApplicationEntity.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    void save_shouldReturnApplication_whenSuccessful() {
        when(mapper.toEntity(application)).thenReturn(applicationEntity);
        when(repository.save(applicationEntity)).thenReturn(Mono.just(applicationEntity));
        when(mapper.toDomain(applicationEntity)).thenReturn(application);

        when(transactionalOperator.execute(any(TransactionCallback.class)))
                .thenAnswer(invocation -> {
                    TransactionCallback<ApplicationEntity> callback =
                            invocation.getArgument(0);
                    return Flux.defer(() -> callback.doInTransaction(mock(ReactiveTransaction.class)));
                });

        Mono<Application> result = adapter.save(application);

        StepVerifier.create(result)
                .expectNext(application)
                .verifyComplete();

        verify(mapper).toEntity(application);
        verify(repository).save(applicationEntity);
        verify(mapper).toDomain(applicationEntity);
    }


    @Test
    void save_shouldPropagateError_whenRepositoryFails() {
        when(mapper.toEntity(application)).thenReturn(applicationEntity);

        when(transactionalOperator.execute(any()))
                .thenReturn(Flux.error(new RuntimeException("DB error")));

        Mono<Application> result = adapter.save(application);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("DB error"))
                .verify();

        verify(mapper).toEntity(application);
        verify(repository, never()).save(any());
        verify(mapper, never()).toDomain(any());
    }
}