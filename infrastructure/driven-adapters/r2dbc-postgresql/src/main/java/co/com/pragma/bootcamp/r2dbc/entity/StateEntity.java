package co.com.pragma.bootcamp.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table("states")
@NoArgsConstructor
@AllArgsConstructor
public class StateEntity {
    @Id
    private Integer stateId;
    private String name;
    private String description;
}
