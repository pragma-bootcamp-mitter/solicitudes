package co.com.pragma.bootcamp.api.mapper;

import co.com.pragma.bootcamp.api.dto.SolicitudRequest;
import co.com.pragma.bootcamp.api.dto.SolicitudResponse;
import co.com.pragma.bootcamp.model.solicitud.Solicitud;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SolicitudDtoMapper {

    // Request -> Dominio
    @Mapping(target = "estado.id", source = "estado.id")
    @Mapping(target = "tipoPrestamo.id", source = "tipoPrestamo.id")
    Solicitud toDomain(SolicitudRequest request);

    // Dominio -> Response
    @Mapping(target = "idEstado", source = "estado.id")
    @Mapping(target = "idTipoPrestamo", source = "tipoPrestamo.id")
    SolicitudResponse toResponse(Solicitud solicitud);
}

