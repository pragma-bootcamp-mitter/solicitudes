package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.model.estado.Estado;
import co.com.pragma.bootcamp.model.solicitud.Solicitud;
import co.com.pragma.bootcamp.model.tipoprestamo.TipoPrestamo;
import co.com.pragma.bootcamp.r2dbc.entity.SolicitudData;
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
class SolicitudRepositoryAdapterTest {

    @Mock
    private SolicitudDataRepository repository;

    @Mock
    private SolicitudMapper mapper;

    @InjectMocks
    private SolicitudRepositoryAdapter adapter;

    private Solicitud solicitud;
    private SolicitudData solicitudData;

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

        solicitudData = new SolicitudData(
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
        SolicitudData solicitudData2 = new SolicitudData(
                "2",
                "123456789",
                BigDecimal.valueOf(2000000),
                24,
                "test2@example.com",
                2,
                2);

        when(repository.findByDocumentoCliente("123456789"))
                .thenReturn(Flux.just(solicitudData, solicitudData2));
        when(mapper.toDomain(solicitudData)).thenReturn(solicitud);
        when(mapper.toDomain(solicitudData2)).thenReturn(solicitud2);

        // Act
        Flux<Solicitud> resultado = adapter.findByDocumentoCliente("123456789");

        // Assert
        StepVerifier.create(resultado)
                .expectNext(solicitud, solicitud2)
                .verifyComplete();

        verify(repository).findByDocumentoCliente("123456789");
        verify(mapper, times(2)).toDomain(any(SolicitudData.class));
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
        verify(mapper, times(0)).toDomain(any(SolicitudData.class));
    }

    @Test
    void toData_debeMapearCorrectamente() {
        // Arrange
        when(mapper.toData(solicitud)).thenReturn(solicitudData);

        // Act
        SolicitudData resultado = adapter.toData(solicitud);

        // Assert
        verify(mapper).toData(solicitud);
    }

    @Test
    void toEntity_debeMapearCorrectamente() {
        // Arrange
        when(mapper.toDomain(solicitudData)).thenReturn(solicitud);

        // Act
        Solicitud resultado = adapter.toEntity(solicitudData);

        // Assert
        verify(mapper).toDomain(solicitudData);
    }
}