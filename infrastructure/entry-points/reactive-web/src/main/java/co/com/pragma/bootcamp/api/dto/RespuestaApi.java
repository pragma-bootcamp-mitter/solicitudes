package co.com.pragma.bootcamp.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RespuestaApi<T> {
    private boolean exito;
    private String mensaje;
    private T datos;

    public static <T> RespuestaApi<T> ok(String mensaje, T datos) {
        return new RespuestaApi<>(true, mensaje, datos);
    }

    public static <T> RespuestaApi<T> error(String mensaje) {
        return new RespuestaApi<>(false, mensaje, null);
    }

    public static <T> RespuestaApi<T> error(String mensaje, T datos) {
        return new RespuestaApi<>(false, mensaje, datos);
    }
}