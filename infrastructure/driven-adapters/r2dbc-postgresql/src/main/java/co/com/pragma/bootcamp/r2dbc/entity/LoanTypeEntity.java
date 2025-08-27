package co.com.pragma.bootcamp.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;

@Data
@Builder
@Table("loan_types")
@NoArgsConstructor
@AllArgsConstructor
public class LoanTypeEntity {
    @Id
    private Integer loanTypeId;
    private String name;
    private BigDecimal minimumAmount;
    private BigDecimal maximumAmount;
    private BigDecimal interestRate;
    private Boolean automaticValidation;
}