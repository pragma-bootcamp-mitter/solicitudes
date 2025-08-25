package co.com.pragma.bootcamp.usecase.registrarsolicitud.helper;

import co.com.pragma.bootcamp.model.estado.Estado;

public enum EstadoSolicitud {
    PENDIENTE_REVISION(1, "PENDIENTE_REVISION", "Solicitud pendiente de revisión"),
    APROBADA(2, "APROBADA", "Solicitud aprobada"),
    RECHAZADA(3, "RECHAZADA", "Solicitud rechazada");

    private final Integer id;
    private final String nombre;
    private final String descripcion;

    EstadoSolicitud(Integer id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Estado toDomain() {
        return Estado.builder()
                .id(id)
                .nombre(nombre)
                .descripcion(descripcion)
                .build();
    }
}