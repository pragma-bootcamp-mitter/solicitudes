package co.com.pragma.bootcamp.model.application;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Application  {
    private String id;
    private String clientDocument;
    private BigDecimal amount;
    private Integer termMonths;
    private String email;
    private Integer stateId;
    private Integer loanTypeId;
}
