package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.model.estado.Estado;
import co.com.pragma.bootcamp.model.solicitud.Solicitud;
import co.com.pragma.bootcamp.model.tipoprestamo.TipoPrestamo;
import co.com.pragma.bootcamp.r2dbc.entidad.EntidadSolicitud;
import co.com.pragma.bootcamp.r2dbc.mapper.SolicitudMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepositorioSolicitudAdapterTest {

    @Mock
    private RepositorioEntidadSolicitud repository;

    @Mock
    private SolicitudMapper mapper;

    @InjectMocks
    private RepositorioSolicitudAdapter adapter;

    private Solicitud solicitud;
    private EntidadSolicitud entidadSolicitud;

    @BeforeEach
    void setup() {
        solicitud = Solicitud.builder()
                .id("1")
                .documentoCliente("123456789")
                .monto(BigDecimal.valueOf(1000000))
                .plazoMeses(12)
                .email("test@example.com")
                .estado(Estado.builder().id(1).nombre("Pendiente").build())
                .tipoPrestamo(TipoPrestamo.builder().id(1).nombre("Hipoteca").build())
                .build();

        entidadSolicitud = new EntidadSolicitud(
                "1",
                "123456789",
                BigDecimal.valueOf(1000000),
                12,
                "test@example.com",
                1,
                1
        );
    }

    @Test
    void findByDocumentoCliente_debeRetornarSolicitudes_cuandoSonEncontradas() {
        // Arrange
        Solicitud solicitud2 = Solicitud.builder()
                .id("2")
                .documentoCliente("123456789")
                .monto(BigDecimal.valueOf(2000000))
                .build();
        EntidadSolicitud entidadSolicitud2 = new EntidadSolicitud(
                "2",
                "123456789",
                BigDecimal.valueOf(2000000),
                24,
                "test2@example.com",
                2,
                2);

        when(repository.findByDocumentoCliente("123456789"))
                .thenReturn(Flux.just(entidadSolicitud, entidadSolicitud2));
        when(mapper.toDomain(entidadSolicitud)).thenReturn(solicitud);
        when(mapper.toDomain(entidadSolicitud2)).thenReturn(solicitud2);

        // Act
        Flux<Solicitud> resultado = adapter.findByDocumentoCliente("123456789");

        // Assert
        StepVerifier.create(resultado)
                .expectNext(solicitud, solicitud2)
                .verifyComplete();

        verify(repository).findByDocumentoCliente("123456789");
        verify(mapper, times(2)).toDomain(any(EntidadSolicitud.class));
    }

    @Test
    void findByDocumentoCliente_debeRetornarMonoVacio_cuandoNoHaySolicitudes() {
        // Arrange
        when(repository.findByDocumentoCliente("987654321"))
                .thenReturn(Flux.empty());

        // Act
        Flux<Solicitud> resultado = adapter.findByDocumentoCliente("987654321");

        // Assert
        StepVerifier.create(resultado)
                .expectNextCount(0)
                .verifyComplete();

        verify(repository).findByDocumentoCliente("987654321");
        verify(mapper, times(0)).toDomain(any(EntidadSolicitud.class));
    }

    @Test
    void toData_debeMapearCorrectamente() {
        // Arrange
        when(mapper.toData(solicitud)).thenReturn(entidadSolicitud);

        // Act
        EntidadSolicitud resultado = adapter.toData(solicitud);

        // Assert
        verify(mapper).toData(solicitud);
    }

    @Test
    void toEntity_debeMapearCorrectamente() {
        // Arrange
        when(mapper.toDomain(entidadSolicitud)).thenReturn(solicitud);

        // Act
        Solicitud resultado = adapter.toEntity(entidadSolicitud);

        // Assert
        verify(mapper).toDomain(entidadSolicitud);
    }
}