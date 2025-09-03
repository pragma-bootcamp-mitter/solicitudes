package co.com.pragma.bootcamp.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;

@Table("application")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationEntity {

    @Id
    @Column("application_id")
    private String id;

    @Column("client_document")
    private String clientDocument;

    @Column("amount")
    private BigDecimal amount;

    @Column("term_months")
    private Integer termMonths;

    @Column("email")
    private String email;

    @Column("state_id")
    private Integer stateId;

    @Column("loan_type_id")
    private Integer loanTypeId;
}
