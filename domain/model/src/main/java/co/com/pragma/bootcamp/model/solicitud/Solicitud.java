package co.com.pragma.bootcamp.model.solicitud;

import co.com.pragma.bootcamp.model.estado.Estado;
import co.com.pragma.bootcamp.model.tipoprestamo.TipoPrestamo;
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
public class Solicitud {
    private String id;
    private String documentoCliente;
    private BigDecimal monto;
    private Integer plazoMeses;
    private String email;
    private Estado estado;
    private TipoPrestamo tipoPrestamo;
}
