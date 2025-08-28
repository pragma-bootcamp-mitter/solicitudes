package co.com.pragma.bootcamp.usecase.registrarsolicitud;

import co.com.pragma.bootcamp.model.application.Application;
import co.com.pragma.bootcamp.model.application.gateways.ApplicationRepository;
import co.com.pragma.bootcamp.model.loantype.LoanType;
import co.com.pragma.bootcamp.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.model.user.gateways.AuthRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static co.com.pragma.bootcamp.model.exceptions.ApplicationErrors.AMOUNT_OUT_OF_RANGE;
import static co.com.pragma.bootcamp.model.exceptions.ApplicationErrors.CLIENT_NOT_FOUND;
import static co.com.pragma.bootcamp.model.exceptions.ApplicationErrors.LOAN_TYPE_DOES_NOT_EXIST;
import static co.com.pragma.bootcamp.usecase.registrarsolicitud.helper.ApplicationState.PENDING_REVIEW;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class RegisterApplicationUseCaseTest {

    private RegisterApplicationUseCase useCase;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private LoanTypeRepository loanTypeRepository;

    @Mock
    private AuthRepository authRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new RegisterApplicationUseCase(applicationRepository, loanTypeRepository, authRepository);
    }

    @Test
    void register_shouldFailWhenLoanTypeDoesNotExist() {
        Application application = new Application();
        application.setAmount(BigDecimal.valueOf(1000));
        application.setTermMonths(12);
        LoanType loanType = new LoanType();
        loanType.setId(1);
        application.setLoanType(loanType);

        when(loanTypeRepository.findById(1)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.register(application))
                .expectErrorMessage(LOAN_TYPE_DOES_NOT_EXIST.getMessage())
                .verify();
    }

    @Test
    void register_shouldFailWhenAmountIsOutOfRange() {
        Application application = new Application();
        application.setAmount(BigDecimal.valueOf(5000));
        application.setTermMonths(12);
        LoanType loanType = new LoanType();
        loanType.setId(1);
        loanType.setMinimumAmount(BigDecimal.valueOf(10000));
        loanType.setMaximumAmount(BigDecimal.valueOf(20000));
        application.setLoanType(loanType);

        when(loanTypeRepository.findById(1)).thenReturn(Mono.just(loanType));

        StepVerifier.create(useCase.register(application))
                .expectErrorMessage(AMOUNT_OUT_OF_RANGE.getMessage())
                .verify();
    }

    @Test
    void register_shouldFailWhenClientIsNotFound() {
        Application application = new Application();
        application.setAmount(BigDecimal.valueOf(15000));
        application.setTermMonths(12);
        LoanType loanType = new LoanType();
        loanType.setId(1);
        loanType.setMinimumAmount(BigDecimal.valueOf(10000));
        loanType.setMaximumAmount(BigDecimal.valueOf(20000));
        application.setLoanType(loanType);

        when(loanTypeRepository.findById(1)).thenReturn(Mono.just(loanType));
        when(authRepository.getUserByDocument(any())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.register(application))
                .expectErrorMessage(CLIENT_NOT_FOUND.getMessage())
                .verify();
    }

    @Test
    void register_shouldSaveApplicationOnSuccess() {
        Application application = new Application();
        application.setClientDocument("123456");
        application.setAmount(BigDecimal.valueOf(15000));
        application.setTermMonths(12);
        LoanType loanType = new LoanType();
        loanType.setId(1);
        loanType.setMinimumAmount(BigDecimal.valueOf(10000));
        loanType.setMaximumAmount(BigDecimal.valueOf(20000));
        application.setLoanType(loanType);

        User user = new User();
        user.setId("u1");

        when(loanTypeRepository.findById(1)).thenReturn(Mono.just(loanType));
        when(authRepository.getUserByDocument("123456")).thenReturn(Mono.just(user));
        when(applicationRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(useCase.register(application))
                .expectNextMatches(s -> s.getState().getId().equals(PENDING_REVIEW.toDomain().getId()))
                .verifyComplete();
    }
}
