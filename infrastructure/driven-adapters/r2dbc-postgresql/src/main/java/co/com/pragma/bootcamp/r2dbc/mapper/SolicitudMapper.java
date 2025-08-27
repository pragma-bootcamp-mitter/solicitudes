package co.com.pragma.bootcamp.r2dbc.mapper;

import co.com.pragma.bootcamp.model.solicitud.Solicitud;
import co.com.pragma.bootcamp.r2dbc.entidad.EntidadSolicitud;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SolicitudMapper {
    // Domain → Data
    @Mapping(target = "idEstado", source = "estado.id")
    @Mapping(target = "idTipoPrestamo", source = "tipoPrestamo.id")
    EntidadSolicitud toData(Solicitud solicitud);

    // Data → Domain
    @Mapping(target = "estado.id", source = "idEstado")
    @Mapping(target = "tipoPrestamo.id", source = "idTipoPrestamo")
    Solicitud toDomain(EntidadSolicitud entidadSolicitud);
}
