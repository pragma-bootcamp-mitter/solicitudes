package co.com.pragma.bootcamp.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationRequest {
    @NotBlank(message = "The client document is mandatory")
    private String clientDocument;

    @NotNull(message = "The amount is mandatory")
    @Positive(message = "The amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "The term in months is mandatory")
    @Positive(message = "The term in months must be greater than zero")
    private Integer termMonths;

    @NotBlank(message = "The email is mandatory")
    @Email(message = "The email must have a valid format")
    private String email;

    private StateRequest state;

    @NotNull(message = "The loan type is mandatory")
    @Valid
    private LoanTypeRequest loanType;

    @Data
    public static class StateRequest {
        @NotNull(message = "The state id is mandatory")
        private Integer id;
    }

    @Data
    public static class LoanTypeRequest {
        @NotNull(message = "The loan type id is mandatory")
        private Integer id;
    }
}