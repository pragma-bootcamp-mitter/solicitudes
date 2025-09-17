package co.com.pragma.bootcamp.model.applicationsummary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ApplicationSummary {
    private String id;
    private BigDecimal amount;
    private Integer termMonths;
    private String email;
    private String clientName;
    private String loanTypeName;
    private BigDecimal interestRate;
    private String stateName;
    private BigDecimal baseSalary;
    private BigDecimal totalMonthlyDebt;
}