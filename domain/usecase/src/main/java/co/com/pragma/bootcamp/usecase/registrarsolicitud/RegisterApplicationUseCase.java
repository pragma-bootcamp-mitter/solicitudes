package co.com.pragma.bootcamp.usecase.registrarsolicitud;

import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.model.application.Application;
import co.com.pragma.bootcamp.model.application.gateways.ApplicationRepository;
import co.com.pragma.bootcamp.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.bootcamp.model.user.gateways.AuthRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;

import static co.com.pragma.bootcamp.model.exceptions.ApplicationErrors.CLIENT_NOT_FOUND;
import static co.com.pragma.bootcamp.model.exceptions.ApplicationErrors.AMOUNT_OUT_OF_RANGE;
import static co.com.pragma.bootcamp.model.exceptions.ApplicationErrors.LOAN_TYPE_DOES_NOT_EXIST;
import static co.com.pragma.bootcamp.usecase.registrarsolicitud.helper.ApplicationState.PENDING_REVIEW;

@RequiredArgsConstructor
public class RegisterApplicationUseCase {

    private final ApplicationRepository applicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final AuthRepository authRepository;

    public Mono<Application> register(Application application) {
        return loanTypeRepository.findById(application.getLoanType().getId())
                .switchIfEmpty(Mono.error(new BusinessException(LOAN_TYPE_DOES_NOT_EXIST)))
                .filter(loanType -> {
                    BigDecimal amount = application.getAmount();
                    return amount.compareTo(loanType.getMinimumAmount()) >= 0 && amount.compareTo(loanType.getMaximumAmount()) <= 0;
                })
                .switchIfEmpty(Mono.error(new BusinessException(AMOUNT_OUT_OF_RANGE)))
                .flatMap(loanType -> authRepository.getUserByDocument(application.getClientDocument()))
                .switchIfEmpty(Mono.error(new BusinessException(CLIENT_NOT_FOUND)))
                .doOnNext(user -> application.setState(PENDING_REVIEW.toDomain()))
                .flatMap(user -> applicationRepository.save(application));
    }
}