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
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static co.com.pragma.bootcamp.model.exceptions.ApplicationErrors.STATE_NOT_FOUND;
import static co.com.pragma.bootcamp.usecase.registerapplication.helper.DomainConstants.APPROVED_STATE;

@RequiredArgsConstructor
public class ListApplicationsUseCase {

    private final ApplicationRepository applicationRepository;
    private final AuthRepository authRepository;
    private final StateRepository stateRepository;
    private final LoanTypeRepository loanTypeRepository;

    public Mono<PageModel<ApplicationSummary>> listByState(int size, int page, String stateName) {
        Pagination pagination = Pagination.builder().page(page).size(size).build();

        return stateRepository.findByName(stateName)
                .switchIfEmpty(Mono.error(new BusinessException(STATE_NOT_FOUND)))
                .flatMap(state -> applicationRepository.findByStateIdAndPagination(pagination, state.getId()))
                .flatMap(pagedApplications -> {
                    Mono<List<ApplicationSummary>> summariesMono = Flux.fromIterable(pagedApplications.getContent())
                            .flatMap(this::enrichAndMapApplicationToSummary)
                            .collectList();

                    return summariesMono.map(summaries -> PageModel.<ApplicationSummary>builder()
                            .content(summaries)
                            .page(pagedApplications.getPage())
                            .size(pagedApplications.getSize())
                            .totalElements(pagedApplications.getTotalElements())
                            .totalPages(pagedApplications.getTotalPages())
                            .build());
                });
    }

    private Mono<ApplicationSummary> enrichAndMapApplicationToSummary(Application application) {
        Mono<User> userMono = authRepository.getUserByDocument(application.getClientDocument());
        Mono<State> stateMono = stateRepository.findById(application.getStateId());
        Mono<LoanType> loanTypeMono = loanTypeRepository.findById(application.getLoanTypeId());
        Mono<BigDecimal> totalDebtMono = calculateTotalDebt(application.getClientDocument());

        return Mono.zip(userMono, totalDebtMono, stateMono, loanTypeMono)
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
        return stateRepository.findByName(APPROVED_STATE)
                .switchIfEmpty(Mono.error(new BusinessException(STATE_NOT_FOUND)))
                .flatMapMany(approvedState -> applicationRepository
                        .findByClientDocumentAndStateId(clientDocument, approvedState.getId()))
                .collectList()
                .map(approvedApplications ->
                        approvedApplications.stream()
                                .map(app -> app.getAmount()
                                        .divide(new BigDecimal(app.getTermMonths()), 2, RoundingMode.HALF_UP))
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                );
    }
}
