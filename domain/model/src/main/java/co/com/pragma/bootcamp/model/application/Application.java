package co.com.pragma.bootcamp.model.application;

import co.com.pragma.bootcamp.model.state.State;
import co.com.pragma.bootcamp.model.loantype.LoanType;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Application  {
    private String id;
    private String clientDocument;
    private BigDecimal amount;
    private Integer termMonths;
    private String email;
    private State state;
    private LoanType loanType;
}
