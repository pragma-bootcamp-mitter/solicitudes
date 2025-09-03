package co.com.pragma.bootcamp.usecase.registrarsolicitud;

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
import static co.com.pragma.bootcamp.usecase.registrarsolicitud.helper.DomainConstants.CLIENT;
import static co.com.pragma.bootcamp.usecase.registrarsolicitud.helper.DomainConstants.PENDING_REVIEW_STATE;

@RequiredArgsConstructor
public class RegisterApplicationUseCase {

    private final ApplicationRepository applicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final AuthRepository authRepository;
    private final StateRepository stateRepository;

    public Mono<Application> register(Application application, String authenticatedDocument, String authenticatedRole) {
        Integer loanTypeId = application.getLoanType().getId();
        String clientDocument = application.getClientDocument();

        if (CLIENT.equals(authenticatedRole) && !clientDocument.equals(authenticatedDocument)) {
            return Mono.error(new BusinessException(UNAUTHORIZED_OPERATION));
        }

        Mono<LoanType> loanTypeMono = loanTypeRepository.findById(loanTypeId)
                .switchIfEmpty(Mono.error(new BusinessException(LOAN_TYPE_DOES_NOT_EXIST)));

        Mono<User> userMono = authRepository.getUserByDocument(clientDocument)
                .switchIfEmpty(Mono.error(new BusinessException(CLIENT_NOT_FOUND)));

        Mono<State> stateMono = stateRepository.findByName(PENDING_REVIEW_STATE)
                .switchIfEmpty(Mono.error(new BusinessException(STATE_NOT_FOUND)));

        return Mono.zip(loanTypeMono, userMono, stateMono)
                .filter(tuple -> {
                    LoanType loanType = tuple.getT1();
                    BigDecimal amount = application.getAmount();
                    return amount.compareTo(loanType.getMinimumAmount()) >= 0
                            && amount.compareTo(loanType.getMaximumAmount()) <= 0;
                })
                .switchIfEmpty(Mono.error(new BusinessException(AMOUNT_OUT_OF_RANGE)))
                .flatMap(tuple -> {
                    LoanType loanType = tuple.getT1();
                    State state = tuple.getT3();
                    Application newApplication = application.toBuilder()
                            .state(state)
                            .loanType(loanType)
                            .build();
                    return applicationRepository.save(newApplication);
                });
    }
}