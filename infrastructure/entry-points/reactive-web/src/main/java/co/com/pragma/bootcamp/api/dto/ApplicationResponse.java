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
public class ApplicationResponse {
    private String id;
    private String clientDocument;
    private BigDecimal amount;
    private Integer termMonths;
    private Integer loanTypeId;
    private Integer stateId;
}
