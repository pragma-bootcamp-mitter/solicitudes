package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.dto.SolicitudRequest;
import co.com.pragma.bootcamp.api.mapper.SolicitudDtoMapper;
import co.com.pragma.bootcamp.usecase.registrarsolicitud.RegistrarSolicitudUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class Handler {

    private final RegistrarSolicitudUseCase useCase;
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
                        ServerResponse.status(201)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response)
                )
                .onErrorResume(e -> {
                    log.error("Error al crear solicitud: {}", e.getMessage(), e);
                    return ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("error", e.getMessage()));
                });
    }
}
