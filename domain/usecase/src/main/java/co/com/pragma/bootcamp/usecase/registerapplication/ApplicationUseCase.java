package co.com.pragma.bootcamp.usecase.registerapplication;

import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.model.application.Application;
import co.com.pragma.bootcamp.model.application.gateways.ApplicationRepository;
import co.com.pragma.bootcamp.model.loantype.LoanType;
import co.com.pragma.bootcamp.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.bootcamp.model.state.State;
import co.com.pragma.bootcamp.model.state.gateways.StateRepository;
import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.model.user.gateways.AuthRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;

import static co.com.pragma.bootcamp.model.exceptions.ApplicationErrors.CLIENT_NOT_FOUND;
import static co.com.pragma.bootcamp.model.exceptions.ApplicationErrors.AMOUNT_OUT_OF_RANGE;
import static co.com.pragma.bootcamp.model.exceptions.ApplicationErrors.LOAN_TYPE_DOES_NOT_EXIST;
import static co.com.pragma.bootcamp.model.exceptions.ApplicationErrors.STATE_NOT_FOUND;
import static co.com.pragma.bootcamp.model.exceptions.ApplicationErrors.UNAUTHORIZED_OPERATION;
import static co.com.pragma.bootcamp.usecase.registerapplication.helper.DomainConstants.CLIENT;
import static co.com.pragma.bootcamp.usecase.registerapplication.helper.DomainConstants.PENDING_REVIEW_STATE;

@RequiredArgsConstructor
public class ApplicationUseCase {

    private final ApplicationRepository applicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final AuthRepository authRepository;
    private final StateRepository stateRepository;

    public Mono<Application> register(Application application, String authenticatedDocument, String authenticatedRole) {
        return validateAuthorization(application, authenticatedDocument, authenticatedRole)
                .then(Mono.zip(
                        getLoanType(application.getLoanTypeId()),
                        getClient(application.getClientDocument()),
                        getPendingReviewState()
                ))
                .flatMap(tuple -> validateAmount(application.getAmount(), tuple.getT1())
                        .thenReturn(tuple)
                )
                .flatMap(tuple -> saveApplication(application, tuple.getT1(), tuple.getT3()));
    }

    private Mono<Void> validateAuthorization(Application application, String authenticatedDocument, String authenticatedRole) {
        if (CLIENT.equals(authenticatedRole) && !application.getClientDocument().equals(authenticatedDocument)) {
            return Mono.error(new BusinessException(UNAUTHORIZED_OPERATION));
        }
        return Mono.empty();
    }

    private Mono<LoanType> getLoanType(Integer loanTypeId) {
        return loanTypeRepository.findById(loanTypeId)
                .switchIfEmpty(Mono.error(new BusinessException(LOAN_TYPE_DOES_NOT_EXIST)));
    }

    private Mono<User> getClient(String clientDocument) {
        return authRepository.getUserByDocument(clientDocument)
                .switchIfEmpty(Mono.error(new BusinessException(CLIENT_NOT_FOUND)));
    }

    private Mono<State> getPendingReviewState() {
        return stateRepository.findByName(PENDING_REVIEW_STATE)
                .switchIfEmpty(Mono.error(new BusinessException(STATE_NOT_FOUND)));
    }

    private Mono<Void> validateAmount(BigDecimal amount, LoanType loanType) {
        boolean valid = amount.compareTo(loanType.getMinimumAmount()) >= 0 &&
                amount.compareTo(loanType.getMaximumAmount()) <= 0;
        if (!valid) {
            return Mono.error(new BusinessException(AMOUNT_OUT_OF_RANGE));
        }
        return Mono.empty();
    }

    private Mono<Application> saveApplication(Application application, LoanType loanType, State state) {
        Application newApplication = application.toBuilder()
                .stateId(state.getId())
                .loanTypeId(loanType.getId())
                .build();
        return applicationRepository.save(newApplication);
    }
}