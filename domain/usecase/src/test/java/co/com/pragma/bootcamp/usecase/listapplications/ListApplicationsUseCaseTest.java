package co.com.pragma.bootcamp.usecase.listapplications;

import co.com.pragma.bootcamp.model.application.Application;
import co.com.pragma.bootcamp.model.application.gateways.ApplicationRepository;
import co.com.pragma.bootcamp.model.applicationsummary.Pagination;
import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.model.loantype.LoanType;
import co.com.pragma.bootcamp.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.bootcamp.model.state.State;
import co.com.pragma.bootcamp.model.state.gateways.StateRepository;
import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.model.user.gateways.AuthRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import static co.com.pragma.bootcamp.model.exceptions.ApplicationErrors.STATE_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListApplicationsUseCaseTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private StateRepository stateRepository;

    @Mock
    private LoanTypeRepository loanTypeRepository;

    @InjectMocks
    private ListApplicationsUseCase useCase;

    private State pendingReviewState;
    private State approvedState;
    private Application application1;
    private Application application2;
    private User user;
    private LoanType loanType;
    private Pagination pagination;

    @BeforeEach
    void setUp() {

        pagination = Pagination.builder().page(0).size(10).build();

        pendingReviewState = State.builder().id(1).name("PENDING_REVIEW").build();
        approvedState = State.builder().id(2).name("APPROVED").build();

        application1 = Application.builder()
                .id("app1")
                .clientDocument("123456")
                .amount(BigDecimal.valueOf(12000))
                .termMonths(12)
                .email("test1@test.com")
                .stateId(1)
                .loanTypeId(1)
                .build();

        application2 = Application.builder()
                .id("app2")
                .clientDocument("123456")
                .amount(BigDecimal.valueOf(24000))
                .termMonths(24)
                .email("test2@test.com")
                .stateId(2)
                .loanTypeId(1)
                .build();

        user = User.builder()
                .identificationDocument("123456")
                .firstName("John")
                .baseSalary(BigDecimal.valueOf(5000))
                .build();

        loanType = LoanType.builder()
                .id(1)
                .name("Automobile Loan")
                .interestRate(BigDecimal.valueOf(0.05))
                .build();
    }

    @Test
    void listAll_shouldReturnApplicationsSummary_whenApplicationsExist() {
        when(stateRepository.findByName("PENDING_REVIEW")).thenReturn(Mono.just(pendingReviewState));
        when(applicationRepository.findByStateIdAndPagination(anyInt(), any(Pagination.class)))
                .thenReturn(Flux.just(application1));
        when(authRepository.getUserByDocument(anyString())).thenReturn(Mono.just(user));
        when(stateRepository.findById(anyInt())).thenReturn(Mono.just(pendingReviewState));
        when(loanTypeRepository.findById(anyInt())).thenReturn(Mono.just(loanType));
        when(stateRepository.findByName("APPROVED")).thenReturn(Mono.just(approvedState));
        when(applicationRepository.findByClientDocumentAndStateId(anyString(), anyInt()))
                .thenReturn(Flux.just(application2));

        StepVerifier.create(useCase.listAll(pagination, "PENDING_REVIEW"))
                .expectNextMatches(summary -> {
                    return summary.getId().equals("app1") &&
                            summary.getClientName().equals("John") &&
                            summary.getLoanTypeName().equals("Automobile Loan") &&
                            summary.getStateName().equals("PENDING_REVIEW") &&
                            summary.getTotalMonthlyDebt().compareTo(BigDecimal.valueOf(1000.00)) == 0;
                })
                .verifyComplete();
    }

    // ---

    @Test
    void listAll_shouldReturnEmptyFlux_whenNoApplicationsFound() {
        when(stateRepository.findByName("PENDING_REVIEW")).thenReturn(Mono.just(pendingReviewState));
        when(applicationRepository.findByStateIdAndPagination(anyInt(), any(Pagination.class)))
                .thenReturn(Flux.empty());

        StepVerifier.create(useCase.listAll(pagination, "PENDING_REVIEW"))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void listAll_shouldThrowException_whenStateNotFound() {
        when(stateRepository.findByName("NON_EXISTENT_STATE")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.listAll(pagination, "NON_EXISTENT_STATE"))
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                                throwable.getMessage().equals(STATE_NOT_FOUND.getMessage())
                )
                .verify();
    }

    @Test
    void listAll_shouldThrowException_whenEnrichmentDependencyFails() {
        when(stateRepository.findByName("PENDING_REVIEW")).thenReturn(Mono.just(pendingReviewState));
        when(applicationRepository.findByStateIdAndPagination(anyInt(), any(Pagination.class)))
                .thenReturn(Flux.just(application1));

        when(authRepository.getUserByDocument(anyString())).thenReturn(Mono.error(new RuntimeException("Auth service is down")));

        StepVerifier.create(useCase.listAll(pagination, "PENDING_REVIEW"))
                .expectError(RuntimeException.class)
                .verify();
    }
}