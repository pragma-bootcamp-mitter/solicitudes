package co.com.pragma.bootcamp.usecase.registrarsolicitud;

import co.com.pragma.bootcamp.model.solicitud.Solicitud;
import co.com.pragma.bootcamp.model.solicitud.gateways.SolicitudRepository;
import co.com.pragma.bootcamp.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.pragma.bootcamp.model.user.gateways.AuthRepository;
import co.com.pragma.bootcamp.usecase.registrarsolicitud.helper.EstadoSolicitud;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;

@RequiredArgsConstructor
public class RegistrarSolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;
    private final AuthRepository authRepository;

    public Mono<Solicitud> registrar(Solicitud solicitud) {

        if (solicitud.getMonto() == null || solicitud.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            return Mono.error(new IllegalArgumentException("Monto inválido"));
        }
        if (solicitud.getPlazoMeses() == null || solicitud.getPlazoMeses() <= 0) {
            return Mono.error(new IllegalArgumentException("Plazo inválido"));
        }
        if (solicitud.getTipoPrestamo() == null || solicitud.getTipoPrestamo().getId() == null) {
            return Mono.error(new IllegalArgumentException("Tipo de préstamo obligatorio"));
        }

        return tipoPrestamoRepository.findById(solicitud.getTipoPrestamo().getId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Tipo de préstamo no existe")))
                .flatMap(tipo -> {
                    BigDecimal monto = solicitud.getMonto();
                    if (monto.compareTo(tipo.getMontoMinimo()) < 0 || monto.compareTo(tipo.getMontoMaximo()) > 0) {
                        return Mono.error(new IllegalArgumentException("Monto fuera de rango para el tipo de préstamo"));
                    }
                    return authRepository.getUserByDocumento(solicitud.getDocumentoCliente())
                            .switchIfEmpty(Mono.error(new IllegalArgumentException("Cliente no encontrado")))
                            .flatMap(user -> {
                                solicitud.setEstado(EstadoSolicitud.PENDIENTE_REVISION.toDomain());
                                return solicitudRepository.save(solicitud);
                            });
                });
    }
}