package co.com.pragma.bootcamp.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;

@Table("solicitud")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudData {

    @Id
    @Column("id_solicitud")
    private String id;

    @Column("documento_cliente")
    private String documentoCliente;

    @Column("monto")
    private BigDecimal monto;

    @Column("plazo_meses")
    private Integer plazoMeses;

    @Column("email")
    private String email;

    @Column("id_estado")
    private Integer idEstado;

    @Column("id_tipo_prestamo")
    private Integer idTipoPrestamo;
}
