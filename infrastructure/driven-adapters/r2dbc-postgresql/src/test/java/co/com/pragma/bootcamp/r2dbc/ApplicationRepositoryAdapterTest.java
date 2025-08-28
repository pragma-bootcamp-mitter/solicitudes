package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.model.state.State;
import co.com.pragma.bootcamp.model.application.Application;
import co.com.pragma.bootcamp.model.loantype.LoanType;
import co.com.pragma.bootcamp.r2dbc.entity.ApplicationEntity;
import co.com.pragma.bootcamp.r2dbc.mapper.ApplicationEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationRepositoryAdapterTest {

    @Mock
    private ApplicationEntityRepository repository;

    @Mock
    private ApplicationEntityMapper mapper;

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
                .state(State.builder().id(1).name("PENDING_REVIEW").build())
                .loanType(LoanType.builder().id(1).name("Mortgage").build())
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
        // Arrange
        Application application2 = Application.builder()
                .id("2")
                .clientDocument("123456789")
                .amount(BigDecimal.valueOf(2000000))
                .build();
        ApplicationEntity applicationEntity2 = new ApplicationEntity(
                "2",
                "123456789",
                BigDecimal.valueOf(2000000),
                24,
                "test2@example.com",
                2,
                2);

        when(repository.findByClientDocument("123456789"))
                .thenReturn(Flux.just(applicationEntity, applicationEntity2));
        when(mapper.toDomain(applicationEntity)).thenReturn(application);
        when(mapper.toDomain(applicationEntity2)).thenReturn(application2);

        // Act
        Flux<Application> result = adapter.findByClientDocument("123456789");

        // Assert
        StepVerifier.create(result)
                .expectNext(application, application2)
                .verifyComplete();

        verify(repository).findByClientDocument("123456789");
        verify(mapper, times(2)).toDomain(any(ApplicationEntity.class));
    }

    @Test
    void findByClientDocument_shouldReturnEmptyMono_whenNoApplicationsFound() {
        // Arrange
        when(repository.findByClientDocument("987654321"))
                .thenReturn(Flux.empty());

        // Act
        Flux<Application> result = adapter.findByClientDocument("987654321");

        // Assert
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();

        verify(repository).findByClientDocument("987654321");
        verify(mapper, times(0)).toDomain(any(ApplicationEntity.class));
    }
}