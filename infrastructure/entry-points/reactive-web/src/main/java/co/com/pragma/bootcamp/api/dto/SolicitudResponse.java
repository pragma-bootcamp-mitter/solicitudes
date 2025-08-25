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
public class SolicitudResponse {
    private String id;
    private String documentoCliente;
    private BigDecimal monto;
    private Integer plazoMeses;
    private Integer idTipoPrestamo;
    private Integer idEstado;
}
