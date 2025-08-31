package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.model.state.State;
import co.com.pragma.bootcamp.r2dbc.adapter.StateAdapter;
import co.com.pragma.bootcamp.r2dbc.entity.StateEntity;
import co.com.pragma.bootcamp.r2dbc.mapper.StateMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StateAdapterTest {

    @Mock
    private StateEntityRepository repository;

    @Mock
    private StateMapper mapper;

    @InjectMocks
    private StateAdapter adapter;

    private StateEntity stateEntity;
    private State stateDomain;

    @BeforeEach
    void setUp() {
        stateEntity = StateEntity.builder()
                .stateId(1)
                .name("PENDING_REVIEW")
                .description("Application is pending review")
                .build();

        stateDomain = State.builder()
                .id(1)
                .name("PENDING_REVIEW")
                .description("Application is pending review")
                .build();
    }

    @Test
    void findById_shouldReturnState_whenFound() {
        when(repository.findByStateId(1)).thenReturn(Mono.just(stateEntity));
        when(mapper.toDomain(stateEntity)).thenReturn(stateDomain);

        Mono<State> result = adapter.findById(1);

        StepVerifier.create(result)
                .expectNext(stateDomain)
                .verifyComplete();

        verify(repository).findByStateId(1);
        verify(mapper).toDomain(stateEntity);
    }

    @Test
    void findById_shouldReturnEmptyMono_whenNotFound() {
        when(repository.findByStateId(2)).thenReturn(Mono.empty());

        Mono<State> result = adapter.findById(2);

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();

        verify(repository).findByStateId(2);
        verify(mapper, never()).toDomain(any());
    }

    @Test
    void findByName_shouldReturnState_whenFound() {
        when(repository.findByName("PENDING_REVIEW")).thenReturn(Mono.just(stateEntity));
        when(mapper.toDomain(stateEntity)).thenReturn(stateDomain);

        Mono<State> result = adapter.findByName("PENDING_REVIEW");

        StepVerifier.create(result)
                .expectNext(stateDomain)
                .verifyComplete();

        verify(repository).findByName("PENDING_REVIEW");
        verify(mapper).toDomain(stateEntity);
    }

    @Test
    void findByName_shouldReturnEmptyMono_whenNotFound() {
        when(repository.findByName("NON_EXISTENT")).thenReturn(Mono.empty());

        Mono<State> result = adapter.findByName("NON_EXISTENT");

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();

        verify(repository).findByName("NON_EXISTENT");
        verify(mapper, never()).toDomain(any());
    }
}