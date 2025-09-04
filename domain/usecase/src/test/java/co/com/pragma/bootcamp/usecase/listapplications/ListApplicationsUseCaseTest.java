package co.com.pragma.bootcamp.usecase.listapplications;

import co.com.pragma.bootcamp.model.application.Application;
import co.com.pragma.bootcamp.model.application.gateways.ApplicationRepository;
import co.com.pragma.bootcamp.model.applicationsummary.ApplicationSummary;
import co.com.pragma.bootcamp.model.applicationsummary.PageModel;
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
import java.util.List;
import static co.com.pragma.bootcamp.model.exceptions.ApplicationErrors.STATE_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
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

    @BeforeEach
    void setUp() {
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
    void listByState_shouldReturnApplicationsSummary_whenApplicationsExist() {
        when(stateRepository.findByName("PENDING_REVIEW")).thenReturn(Mono.just(pendingReviewState));
        when(authRepository.getUserByDocument(anyString())).thenReturn(Mono.just(user));
        when(stateRepository.findById(anyInt())).thenReturn(Mono.just(pendingReviewState));
        when(loanTypeRepository.findById(anyInt())).thenReturn(Mono.just(loanType));
        when(stateRepository.findByName("APPROVED")).thenReturn(Mono.just(approvedState));
        when(applicationRepository.findByClientDocumentAndStateId(anyString(), anyInt()))
                .thenReturn(Flux.just(application2));

        PageModel<Application> pageModel = PageModel.<Application>builder()
                .content(List.of(application1))
                .page(0)
                .size(10)
                .totalElements(1L)
                .totalPages(1)
                .build();
        when(applicationRepository.findByStateIdAndPagination(any(Pagination.class), anyInt()))
                .thenReturn(Mono.just(pageModel));

        StepVerifier.create(useCase.listByState(10, 0, "PENDING_REVIEW"))
                .expectNextMatches(resultPage -> {
                    List<ApplicationSummary> summaries = resultPage.getContent();
                    return resultPage.getPage() == 0 &&
                            resultPage.getSize() == 10 &&
                            resultPage.getTotalElements() == 1L &&
                            resultPage.getTotalPages() == 1 &&
                            summaries.size() == 1 &&
                            summaries.get(0).getId().equals("app1");
                })
                .verifyComplete();
    }



    @Test
    void listByState_shouldReturnEmptyList_whenNoApplicationsFound() {
        when(stateRepository.findByName("PENDING_REVIEW")).thenReturn(Mono.just(pendingReviewState));

        PageModel<Application> emptyPageModel = PageModel.<Application>builder()
                .content(List.of())
                .page(0)
                .size(10)
                .totalElements(0L)
                .totalPages(0)
                .build();
        when(applicationRepository.findByStateIdAndPagination(any(Pagination.class), anyInt()))
                .thenReturn(Mono.just(emptyPageModel));

        StepVerifier.create(useCase.listByState(10, 0, "PENDING_REVIEW"))
                .expectNextMatches(resultPage -> resultPage.getContent().isEmpty() && resultPage.getTotalElements() == 0L)
                .verifyComplete();
    }



    @Test
    void listByState_shouldThrowException_whenStateNotFound() {
        when(stateRepository.findByName("NON_EXISTENT_STATE")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.listByState(10, 0, "NON_EXISTENT_STATE"))
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                                throwable.getMessage().equals(STATE_NOT_FOUND.getMessage())
                )
                .verify();
    }



    @Test
    void listByState_shouldThrowException_whenEnrichmentDependencyFails() {
        when(stateRepository.findByName("PENDING_REVIEW")).thenReturn(Mono.just(pendingReviewState));

        PageModel<Application> pageModel = PageModel.<Application>builder()
                .content(List.of(application1))
                .page(0)
                .size(10)
                .totalElements(1L)
                .totalPages(1)
                .build();
        lenient().when(applicationRepository.findByStateIdAndPagination(any(Pagination.class), anyInt()))
                .thenReturn(Mono.just(pageModel));

        when(authRepository.getUserByDocument(anyString())).thenReturn(Mono.error(new RuntimeException("Auth service is down")));

        StepVerifier.create(useCase.listByState(10, 0, "PENDING_REVIEW"))
                .expectError(RuntimeException.class)
                .verify();
    }
}