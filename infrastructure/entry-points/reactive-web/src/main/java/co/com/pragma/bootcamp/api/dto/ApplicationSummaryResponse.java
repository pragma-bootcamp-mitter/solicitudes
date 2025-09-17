package co.com.pragma.bootcamp.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationSummaryResponse {
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