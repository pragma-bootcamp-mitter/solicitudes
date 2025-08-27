package co.com.pragma.bootcamp.usecase.registrarsolicitud;

import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.model.solicitud.Solicitud;
import co.com.pragma.bootcamp.model.solicitud.gateways.RepositorioSolicitud;
import co.com.pragma.bootcamp.model.tipoprestamo.gateways.RepositorioTipoPrestamo;
import co.com.pragma.bootcamp.model.user.gateways.RepositorioAuth;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;

import static co.com.pragma.bootcamp.usecase.registrarsolicitud.helper.ErroresSolicitud.CLIENTE_NO_ENCONTRADO;
import static co.com.pragma.bootcamp.usecase.registrarsolicitud.helper.ErroresSolicitud.MONTO_FUERA_DE_RANGO;
import static co.com.pragma.bootcamp.usecase.registrarsolicitud.helper.ErroresSolicitud.TIPO_PRESTAMO_NO_EXISTE;
import static co.com.pragma.bootcamp.usecase.registrarsolicitud.helper.EstadoSolicitud.PENDIENTE_REVISION;

@RequiredArgsConstructor
public class RegistrarSolicitudCasoDeUso {

    private final RepositorioSolicitud repositorioSolicitud;
    private final RepositorioTipoPrestamo repositorioTipoPrestamo;
    private final RepositorioAuth repositorioAuth;

    public Mono<Solicitud> registrar(Solicitud solicitud) {

        return repositorioTipoPrestamo.findById(solicitud.getTipoPrestamo().getId())
                .switchIfEmpty(Mono.error(new BusinessException(TIPO_PRESTAMO_NO_EXISTE.getMensaje())))
                .flatMap(tipo -> {
                    BigDecimal monto = solicitud.getMonto();
                    if (monto.compareTo(tipo.getMontoMinimo()) < 0 || monto.compareTo(tipo.getMontoMaximo()) > 0) {
                        return Mono.error(new BusinessException(MONTO_FUERA_DE_RANGO.getMensaje()));
                    }
                    return repositorioAuth.getUserByDocumento(solicitud.getDocumentoCliente())
                            .switchIfEmpty(Mono.error(new BusinessException(CLIENTE_NO_ENCONTRADO.getMensaje())))
                            .flatMap(user -> {
                                solicitud.setEstado(PENDIENTE_REVISION.toDomain());
                                return repositorioSolicitud.save(solicitud);
                            });
                });
    }

}