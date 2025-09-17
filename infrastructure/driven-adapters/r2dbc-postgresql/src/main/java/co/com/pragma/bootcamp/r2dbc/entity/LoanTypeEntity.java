package co.com.pragma.bootcamp.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;

@Table("loan_types")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanTypeEntity {
    @Id
    @Column("loan_type_id")
    private Integer loanTypeId;

    @Column("name")
    private String name;

    @Column("minimum_amount")
    private BigDecimal minimumAmount;

    @Column("maximum_amount")
    private BigDecimal maximumAmount;

    @Column("interest_rate")
    private BigDecimal interestRate;

    @Column("automatic_validation")
    private Boolean automaticValidation;
}