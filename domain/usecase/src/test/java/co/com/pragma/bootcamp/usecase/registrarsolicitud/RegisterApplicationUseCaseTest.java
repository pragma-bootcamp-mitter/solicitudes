package co.com.pragma.bootcamp.usecase.registrarsolicitud;

import co.com.pragma.bootcamp.model.application.Application;
import co.com.pragma.bootcamp.model.application.gateways.ApplicationRepository;
import co.com.pragma.bootcamp.model.loantype.LoanType;
import co.com.pragma.bootcamp.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.bootcamp.model.state.State;
import co.com.pragma.bootcamp.model.state.gateways.StateRepository;
import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.model.user.gateways.AuthRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;

import static co.com.pragma.bootcamp.model.exceptions.ApplicationErrors.AMOUNT_OUT_OF_RANGE;
import static co.com.pragma.bootcamp.model.exceptions.ApplicationErrors.CLIENT_NOT_FOUND;
import static co.com.pragma.bootcamp.model.exceptions.ApplicationErrors.LOAN_TYPE_DOES_NOT_EXIST;
import static co.com.pragma.bootcamp.model.exceptions.ApplicationErrors.STATE_NOT_FOUND;
import static co.com.pragma.bootcamp.usecase.registrarsolicitud.helper.DomainConstants.PENDING_REVIEW_STATE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class RegisterApplicationUseCaseTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private LoanTypeRepository loanTypeRepository;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private StateRepository stateRepository;

    @InjectMocks
    private RegisterApplicationUseCase useCase;

    private Application application;
    private LoanType loanType;
    private User user;
    private State pendingReviewState;

    private final String authenticatedDocument = "101";
    private final String authenticatedRole = "ADMIN";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new RegisterApplicationUseCase(
                applicationRepository,
                loanTypeRepository,
                authRepository,
                stateRepository);

        application = new Application();
        application.setClientDocument("123456");
        application.setAmount(BigDecimal.valueOf(15000));
        application.setTermMonths(12);

        LoanType initialLoanType = new LoanType();
        initialLoanType.setId(1);
        application.setLoanType(initialLoanType);

        loanType = new LoanType();
        loanType.setId(1);
        loanType.setMinimumAmount(BigDecimal.valueOf(10000));
        loanType.setMaximumAmount(BigDecimal.valueOf(20000));

        user = new User();
        user.setId("u1");

        pendingReviewState = new State();
        pendingReviewState.setId(1);
        pendingReviewState.setName(PENDING_REVIEW_STATE);
        pendingReviewState.setDescription("Application is pending review");
    }

    @Test
    void register_shouldSaveApplicationOnSuccess() {
        when(loanTypeRepository.findById(1)).thenReturn(Mono.just(loanType));
        when(authRepository.getUserByDocument("123456")).thenReturn(Mono.just(user));
        when(stateRepository.findByName(PENDING_REVIEW_STATE)).thenReturn(Mono.just(pendingReviewState));
        when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> {
            Application savedApp = invocation.getArgument(0);
            return Mono.just(savedApp);
        });

        StepVerifier.create(useCase.register(application, authenticatedDocument, authenticatedRole))
                .expectNextMatches(savedApplication ->
                        savedApplication.getState().getId().equals(1) &&
                                savedApplication.getLoanType().getId().equals(1) &&
                                savedApplication.getAmount().compareTo(BigDecimal.valueOf(15000)) == 0
                )
                .verifyComplete();
    }

    @Test
    void register_shouldFailWhenPendingReviewStateNotFound() {
        when(loanTypeRepository.findById(1)).thenReturn(Mono.just(loanType));
        when(authRepository.getUserByDocument("123456")).thenReturn(Mono.just(user));
        when(stateRepository.findByName(PENDING_REVIEW_STATE)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.register(application, authenticatedDocument, authenticatedRole))
                .expectErrorMessage(STATE_NOT_FOUND.getMessage())
                .verify();
    }

    @Test
    void register_shouldFailWhenLoanTypeDoesNotExist() {
        when(loanTypeRepository.findById(1)).thenReturn(Mono.empty());
        when(authRepository.getUserByDocument(any())).thenReturn(Mono.just(user));
        when(stateRepository.findByName(PENDING_REVIEW_STATE)).thenReturn(Mono.just(pendingReviewState));

        StepVerifier.create(useCase.register(application, authenticatedDocument, authenticatedRole))
                .expectErrorMessage(LOAN_TYPE_DOES_NOT_EXIST.getMessage())
                .verify();
    }

    @Test
    void register_shouldFailWhenAmountIsOutOfRange() {
        application.setAmount(BigDecimal.valueOf(5000));
        when(loanTypeRepository.findById(1)).thenReturn(Mono.just(loanType));
        when(authRepository.getUserByDocument(any())).thenReturn(Mono.just(user));
        when(stateRepository.findByName(PENDING_REVIEW_STATE)).thenReturn(Mono.just(pendingReviewState));

        StepVerifier.create(useCase.register(application, authenticatedDocument, authenticatedRole))
                .expectErrorMessage(AMOUNT_OUT_OF_RANGE.getMessage())
                .verify();
    }

    @Test
    void register_shouldFailWhenClientIsNotFound() {
        when(loanTypeRepository.findById(1)).thenReturn(Mono.just(loanType));
        when(authRepository.getUserByDocument(any())).thenReturn(Mono.empty());
        when(stateRepository.findByName(PENDING_REVIEW_STATE)).thenReturn(Mono.just(pendingReviewState));

        StepVerifier.create(useCase.register(application, authenticatedDocument, authenticatedRole))
                .expectErrorMessage(CLIENT_NOT_FOUND.getMessage())
                .verify();
    }
}