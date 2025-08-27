package co.com.pragma.bootcamp.api.config;


import co.com.pragma.bootcamp.api.dto.ApiResponse;
import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Mono<ResponseEntity<ApiResponse<Object>>> handleBusiness(BusinessException ex) {
        log.error("Error de negocio: {}", ex.getMessage());
        return Mono.just(
                ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(ApiResponse.error(ex.getMessage()))
        );
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ApiResponse<Map<String, String>>>> handleValidation(WebExchangeBindException ex) {
        log.warn("Error de validación: {}", ex.getMessage());

        var errores = ex.getFieldErrors().stream()
                .collect(Collectors.toMap(
                        e -> e.getField(),
                        e -> e.getDefaultMessage(),
                        (m1, m2) -> m1
                ));

        return Mono.just(
                ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(ApiResponse.error("Error de validación", errores))
        );
    }

    @ExceptionHandler(WebClientResponseException.class)
    public Mono<ResponseEntity<ApiResponse<String>>> handleWebClient(WebClientResponseException ex) {
        log.error("Error en llamada a servicio externo [{}]: {}", ex.getStatusCode(), ex.getMessage());

        HttpStatusCode statusCode = ex.getStatusCode();

        String mensaje;
        if (statusCode.is4xxClientError()) {
            mensaje = "Error en la solicitud al servicio externo";
        } else if (statusCode.is5xxServerError()) {
            mensaje = "Error en el servidor externo";
        } else {
            mensaje = "Error en comunicación con servicio externo";
        }

        return Mono.just(
                ResponseEntity.status(statusCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(ApiResponse.error(mensaje, ex.getResponseBodyAsString()))
        );
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiResponse<Object>>> handleGeneric(Exception ex) {
        log.error("Error inesperado: {}", ex.getMessage(), ex);
        return Mono.just(
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(ApiResponse.error("Ha ocurrido un error inesperado"))
        );
    }
}