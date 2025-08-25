package co.com.pragma.bootcamp.usecase.registrarsolicitud;

import co.com.pragma.bootcamp.model.solicitud.Solicitud;
import co.com.pragma.bootcamp.model.solicitud.gateways.SolicitudRepository;
import co.com.pragma.bootcamp.model.tipoprestamo.TipoPrestamo;
import co.com.pragma.bootcamp.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.model.user.gateways.AuthRepository;
import co.com.pragma.bootcamp.usecase.registrarsolicitud.helper.EstadoSolicitud;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class RegistrarSolicitudUseCaseTest {

    private RegistrarSolicitudUseCase useCase;

    @Mock
    private SolicitudRepository solicitudRepository;

    @Mock
    private TipoPrestamoRepository tipoPrestamoRepository;

    @Mock
    private AuthRepository authRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new RegistrarSolicitudUseCase(solicitudRepository, tipoPrestamoRepository, authRepository);
    }

    @Test
    void registrar_MontoNulo_DeberiaFallar() {
        Solicitud solicitud = new Solicitud();
        solicitud.setMonto(null);
        solicitud.setPlazoMeses(12);

        StepVerifier.create(useCase.registrar(solicitud))
                .expectErrorMessage("Monto inválido")
                .verify();
    }

    @Test
    void registrar_PlazoInvalido_DeberiaFallar() {
        Solicitud solicitud = new Solicitud();
        solicitud.setMonto(BigDecimal.valueOf(1000));
        solicitud.setPlazoMeses(0);

        StepVerifier.create(useCase.registrar(solicitud))
                .expectErrorMessage("Plazo inválido")
                .verify();
    }

    @Test
    void registrar_TipoPrestamoNulo_DeberiaFallar() {
        Solicitud solicitud = new Solicitud();
        solicitud.setMonto(BigDecimal.valueOf(1000));
        solicitud.setPlazoMeses(12);
        solicitud.setTipoPrestamo(null);

        StepVerifier.create(useCase.registrar(solicitud))
                .expectErrorMessage("Tipo de préstamo obligatorio")
                .verify();
    }

    @Test
    void registrar_TipoPrestamoNoExiste_DeberiaFallar() {
        Solicitud solicitud = new Solicitud();
        solicitud.setMonto(BigDecimal.valueOf(1000));
        solicitud.setPlazoMeses(12);
        TipoPrestamo tipo = new TipoPrestamo();
        tipo.setId(1);
        solicitud.setTipoPrestamo(tipo);

        when(tipoPrestamoRepository.findById(1)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.registrar(solicitud))
                .expectErrorMessage("Tipo de préstamo no existe")
                .verify();
    }

    @Test
    void registrar_MontoFueraDeRango_DeberiaFallar() {
        Solicitud solicitud = new Solicitud();
        solicitud.setMonto(BigDecimal.valueOf(5000));
        solicitud.setPlazoMeses(12);
        TipoPrestamo tipo = new TipoPrestamo();
        tipo.setId(1);
        tipo.setMontoMinimo(BigDecimal.valueOf(10000));
        tipo.setMontoMaximo(BigDecimal.valueOf(20000));
        solicitud.setTipoPrestamo(tipo);

        when(tipoPrestamoRepository.findById(1)).thenReturn(Mono.just(tipo));

        StepVerifier.create(useCase.registrar(solicitud))
                .expectErrorMessage("Monto fuera de rango para el tipo de préstamo")
                .verify();
    }

    @Test
    void registrar_ClienteNoEncontrado_DeberiaFallar() {
        Solicitud solicitud = new Solicitud();
        solicitud.setMonto(BigDecimal.valueOf(15000));
        solicitud.setPlazoMeses(12);
        TipoPrestamo tipo = new TipoPrestamo();
        tipo.setId(1);
        tipo.setMontoMinimo(BigDecimal.valueOf(10000));
        tipo.setMontoMaximo(BigDecimal.valueOf(20000));
        solicitud.setTipoPrestamo(tipo);

        when(tipoPrestamoRepository.findById(1)).thenReturn(Mono.just(tipo));
        when(authRepository.getUserByDocumento(any())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.registrar(solicitud))
                .expectErrorMessage("Cliente no encontrado")
                .verify();
    }

    @Test
    void registrar_Exito_DeberiaGuardarSolicitud() {
        Solicitud solicitud = new Solicitud();
        solicitud.setDocumentoCliente("123456");
        solicitud.setMonto(BigDecimal.valueOf(15000));
        solicitud.setPlazoMeses(12);
        TipoPrestamo tipo = new TipoPrestamo();
        tipo.setId(1);
        tipo.setMontoMinimo(BigDecimal.valueOf(10000));
        tipo.setMontoMaximo(BigDecimal.valueOf(20000));
        solicitud.setTipoPrestamo(tipo);

        User user = new User();
        user.setId("u1");

        when(tipoPrestamoRepository.findById(1)).thenReturn(Mono.just(tipo));
        when(authRepository.getUserByDocumento("123456")).thenReturn(Mono.just(user));
        when(solicitudRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(useCase.registrar(solicitud))
                .expectNextMatches(s -> s.getEstado().getId().equals(EstadoSolicitud.PENDIENTE_REVISION.toDomain().getId()))
                .verifyComplete();
    }
}
