package co.com.pragma.bootcamp.r2dbc.mapper;


import co.com.pragma.bootcamp.model.tipoprestamo.TipoPrestamo;
import co.com.pragma.bootcamp.r2dbc.entidad.EntidadTipoPrestamo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TipoPrestamoMapper {
    EntidadTipoPrestamo toData(TipoPrestamo d);
    TipoPrestamo toDomain(EntidadTipoPrestamo e);
}