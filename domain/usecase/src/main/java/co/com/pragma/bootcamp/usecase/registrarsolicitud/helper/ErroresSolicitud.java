package co.com.pragma.bootcamp.usecase.registrarsolicitud.helper;

public enum ErroresSolicitud {
    TIPO_PRESTAMO_NO_EXISTE("Tipo de préstamo no existe"),
    MONTO_FUERA_DE_RANGO("Monto fuera de rango para el tipo de préstamo"),
    CLIENTE_NO_ENCONTRADO("Cliente no encontrado")
    ;

    private final String mensaje;

    ErroresSolicitud(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }
}
