package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.dto.PeticionSolicitud;
import co.com.pragma.bootcamp.api.dto.RespuestaApi;
import co.com.pragma.bootcamp.api.mapper.MapeadorSolicitud;
import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.model.solicitud.gateways.RepositorioSolicitud;
import co.com.pragma.bootcamp.usecase.registrarsolicitud.RegistrarSolicitudCasoDeUso;
import jakarta.validation.ConstraintViolation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import jakarta.validation.Validator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class Handler {

    private final RegistrarSolicitudCasoDeUso useCase;
    private final RepositorioSolicitud repositorioSolicitud;
    private final MapeadorSolicitud mapper;
    private final Validator validator;

    public Mono<ServerResponse> registrar(ServerRequest request) {
        return request.bodyToMono(PeticionSolicitud.class)
                .doOnNext(req -> log.info("PeticionSolicitud recibida: {}", req))
                .flatMap(solicitud -> {
                    Set<ConstraintViolation<PeticionSolicitud>> violations = validator.validate(solicitud);
                    if (!violations.isEmpty()) {
                        Map<String, String> errores = violations.stream()
                                .collect(Collectors.toMap(
                                        v -> v.getPropertyPath().toString(),
                                        ConstraintViolation::getMessage
                                ));
                        log.warn("Errores de validación en solicitud: {}", errores);
                        return ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(RespuestaApi.error("Error de validación", errores));
                    }

                    return useCase.registrar(mapper.aDominio(solicitud))
                            .doOnNext(saved -> log.info("Solicitud registrada en dominio: {}", saved))
                            .map(mapper::aRepuesta)
                            .flatMap(response ->
                                    ServerResponse.status(HttpStatus.CREATED)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .bodyValue(RespuestaApi.ok("Solicitud creada exitosamente", response))
                            );
                })
                .onErrorResume(BusinessException.class, e -> {
                    log.error("Error de negocio al registrar solicitud: {}", e.getMessage());
                    return ServerResponse.status(HttpStatus.CONFLICT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(RespuestaApi.error(e.getMessage()));
                })
                .onErrorResume(e -> {
                    log.error("Error inesperado al registrar solicitud: {}", e.getMessage(), e);
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(RespuestaApi.error("Error interno del servidor"));
                });
    }

    public Mono<ServerResponse> listar(ServerRequest request) {
        return repositorioSolicitud.findAll()
                .map(mapper::aRepuesta)
                .collectList()
                .flatMap(list ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(RespuestaApi.ok("Listado de solicitudes", list))
                )
                .onErrorResume(e -> {
                    log.error("Error al listar solicitudes: {}", e.getMessage(), e);
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(RespuestaApi.error("Error interno al listar solicitudes"));
                });
    }

}
