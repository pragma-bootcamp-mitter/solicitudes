package co.com.pragma.bootcamp.r2dbc.mapper;


import co.com.pragma.bootcamp.model.tipoprestamo.TipoPrestamo;
import co.com.pragma.bootcamp.r2dbc.entity.TipoPrestamoData;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TipoPrestamoMapper {
    TipoPrestamoData toData(TipoPrestamo d);
    TipoPrestamo toDomain(TipoPrestamoData e);
}