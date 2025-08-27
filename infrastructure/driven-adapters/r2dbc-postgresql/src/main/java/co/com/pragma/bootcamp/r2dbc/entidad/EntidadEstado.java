package co.com.pragma.bootcamp.r2dbc.entidad;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table("estados")
@NoArgsConstructor
@AllArgsConstructor
public class EntidadEstado {
    @Id
    private Integer idEstado;
    private String nombre;
    private String descripcion;
}
