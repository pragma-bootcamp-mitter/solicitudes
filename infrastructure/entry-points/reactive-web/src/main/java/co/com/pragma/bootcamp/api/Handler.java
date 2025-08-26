package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.dto.RespuestaApi;
import co.com.pragma.bootcamp.api.dto.SolicitudRequest;
import co.com.pragma.bootcamp.api.mapper.SolicitudDtoMapper;
import co.com.pragma.bootcamp.model.solicitud.gateways.SolicitudRepository;
import co.com.pragma.bootcamp.usecase.registrarsolicitud.RegistrarSolicitudUseCase;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class Handler {

    private final RegistrarSolicitudUseCase useCase;
    private final SolicitudRepository solicitudRepository;
    private final SolicitudDtoMapper mapper;

    public Mono<ServerResponse> registrar(ServerRequest request) {
        return request.bodyToMono(SolicitudRequest.class)
                .doOnNext(req -> log.info("SolicitudRequest recibida: {}", req))
                .map(mapper::toDomain)
                .doOnNext(domain -> log.info("Solicitud mapeada a dominio: {}", domain))
                .flatMap(useCase::registrar)
                .doOnNext(saved -> log.info("Solicitud registrada: {}", saved))
                .map(mapper::toResponse)
                .doOnNext(resp -> log.info("Respuesta generada: {}", resp))
                .flatMap(response ->
                        ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(RespuestaApi.ok("Solicitud creada exitosamente", response))
                )
                .onErrorResume(e -> {
                    log.error("Error al crear solicitud: {}", e.getMessage(), e);
                    return ServerResponse.status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(RespuestaApi.error(e.getMessage()));
                });
    }


    public Mono<ServerResponse> listar(ServerRequest request) {
        return solicitudRepository.findAll()
                .map(mapper::toResponse)
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
                            .bodyValue(RespuestaApi.error(e.getMessage()));
                });
    }

}
