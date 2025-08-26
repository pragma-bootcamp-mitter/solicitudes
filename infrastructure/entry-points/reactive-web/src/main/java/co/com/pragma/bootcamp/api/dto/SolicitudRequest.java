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
public class SolicitudRequest {
    @NotBlank(message = "El documento del cliente es obligatorio")
    private String documentoCliente;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor que cero")
    private BigDecimal monto;

    @NotNull(message = "El plazo en meses es obligatorio")
    @Positive(message = "El plazo en meses debe ser mayor que cero")
    private Integer plazoMeses;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    private EstadoRequest estado;

    @NotNull(message = "El tipo de préstamo es obligatorio")
    @Valid
    private TipoPrestamoRequest tipoPrestamo;

    @Data
    public static class EstadoRequest {
        @NotNull(message = "El id de estado es obligatorio")
        private Integer id;
    }

    @Data
    public static class TipoPrestamoRequest {
        @NotNull(message = "El id del tipo de préstamo es obligatorio")
        private Integer id;
    }
}