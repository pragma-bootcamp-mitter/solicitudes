package co.com.pragma.bootcamp.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudRequest {
    private String documentoCliente;
    private BigDecimal monto;
    private Integer plazoMeses;
    private String email;
    private EstadoRequest estado;
    private TipoPrestamoRequest tipoPrestamo;

    @Data
    public static class EstadoRequest {
        private Integer id;
    }

    @Data
    public static class TipoPrestamoRequest {
        private Integer id;
    }
}