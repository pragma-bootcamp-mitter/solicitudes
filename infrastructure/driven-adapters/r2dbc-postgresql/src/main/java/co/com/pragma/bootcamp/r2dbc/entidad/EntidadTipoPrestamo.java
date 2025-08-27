package co.com.pragma.bootcamp.r2dbc.entidad;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;

@Data
@Builder
@Table("tipo_prestamo")
@NoArgsConstructor
@AllArgsConstructor
public class EntidadTipoPrestamo {
    @Id
    private Integer idTipoPrestamo;
    private String nombre;
    private BigDecimal montoMinimo;
    private BigDecimal montoMaximo;
    private BigDecimal tasaInteres;
    private Boolean validacionAutomatica;
}