package co.com.pragma.bootcamp.usecase.listapplications;

import co.com.pragma.bootcamp.model.application.Application;
import co.com.pragma.bootcamp.model.application.gateways.ApplicationRepository;
import co.com.pragma.bootcamp.model.applicationsummary.ApplicationSummary;
import co.com.pragma.bootcamp.model.applicationsummary.Pagination;
import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.model.loantype.LoanType;
import co.com.pragma.bootcamp.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.bootcamp.model.state.State;
import co.com.pragma.bootcamp.model.state.gateways.StateRepository;
import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.model.user.gateways.AuthRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static co.com.pragma.bootcamp.model.exceptions.ApplicationErrors.STATE_NOT_FOUND;

@RequiredArgsConstructor
public class ListApplicationsUseCase {

    private final ApplicationRepository applicationRepository;
    private final AuthRepository authRepository;
    private final StateRepository stateRepository;
    private final LoanTypeRepository loanTypeRepository;

    public Flux<ApplicationSummary> listAll(Pagination pagination, String stateName) {
        return stateRepository.findByName(stateName)
                .switchIfEmpty(Mono.error(new BusinessException(STATE_NOT_FOUND)))
                .flatMapMany(state -> applicationRepository.findByStateIdAndPagination(state.getId(), pagination))
                .flatMap(this::enrichAndMapApplicationToSummary);
    }

    private Mono<ApplicationSummary> enrichAndMapApplicationToSummary(Application application) {
        return Mono.zip(
                        authRepository.getUserByDocument(application.getClientDocument()),
                        calculateTotalDebt(application.getClientDocument()),
                        stateRepository.findById(application.getStateId()),
                        loanTypeRepository.findById(application.getLoanTypeId())
                )
                .map(tuple -> {
                    User user = tuple.getT1();
                    BigDecimal totalDebt = tuple.getT2();
                    State state = tuple.getT3();
                    LoanType loanType = tuple.getT4();

                    return ApplicationSummary.builder()
                            .id(application.getId())
                            .amount(application.getAmount())
                            .termMonths(application.getTermMonths())
                            .email(application.getEmail())
                            .clientName(user.getFirstName())
                            .loanTypeName(loanType.getName())
                            .interestRate(loanType.getInterestRate())
                            .stateName(state.getName())
                            .baseSalary(user.getBaseSalary())
                            .totalMonthlyDebt(totalDebt)
                            .build();
                });
    }

    private Mono<BigDecimal> calculateTotalDebt(String clientDocument) {
        return stateRepository.findByName("APPROVED")
                .switchIfEmpty(Mono.error(new BusinessException(STATE_NOT_FOUND)))
                .flatMapMany(approvedState -> applicationRepository.findByClientDocumentAndStateId(clientDocument, approvedState.getId()))
                .collectList()
                .map(approvedApplications ->
                        approvedApplications.stream()
                                .map(app -> {
                                    return app.getAmount().divide(new BigDecimal(app.getTermMonths()), 2, RoundingMode.HALF_UP);
                                })
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                );
    }
}
