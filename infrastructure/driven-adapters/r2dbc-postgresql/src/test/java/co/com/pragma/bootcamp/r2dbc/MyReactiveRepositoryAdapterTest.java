package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.model.tipoprestamo.TipoPrestamo;
import co.com.pragma.bootcamp.r2dbc.entity.TipoPrestamoData;
import co.com.pragma.bootcamp.r2dbc.mapper.TipoPrestamoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MyReactiveRepositoryAdapterTest {

    @Mock
    private TipoPrestamoDataRepository repository;

    @Mock
    private TipoPrestamoMapper mapper;

    @InjectMocks
    private TipoPrestamoRepositoryAdapter adapter;

    private TipoPrestamoData tipoPrestamoData;
    private TipoPrestamo tipoPrestamo;

    @BeforeEach
    void setUp() {
        tipoPrestamoData = TipoPrestamoData.builder()
                .idTipoPrestamo(1)
                .nombre("Hipoteca")
                .montoMinimo(BigDecimal.valueOf(1000000))
                .montoMaximo(BigDecimal.valueOf(500000000))
                .tasaInteres(BigDecimal.valueOf(0.05))
                .validacionAutomatica(true)
                .build();

        tipoPrestamo = TipoPrestamo.builder()
                .id(1)
                .nombre("Hipoteca")
                .montoMinimo(BigDecimal.valueOf(1000000))
                .montoMaximo(BigDecimal.valueOf(500000000))
                .tasaInteres(BigDecimal.valueOf(0.05))
                .validacionAutomatica(true)
                .build();
    }

    @Test
    void buscarPorId_debeRetornarTipoPrestamo_cuandoEsEncontrado() {
        when(repository.findById("1"))
                .thenReturn(Mono.just(tipoPrestamoData));
        when(mapper.toDomain(tipoPrestamoData))
                .thenReturn(tipoPrestamo);


        Mono<TipoPrestamo> result = adapter.findById(1);

        StepVerifier.create(result)
                .expectNext(tipoPrestamo)
                .verifyComplete();

        verify(repository).findById("1");
        verify(mapper).toDomain(tipoPrestamoData);
    }

    @Test
    void buscarPorId_debeRetornarMonoVacio_cuandoNoEsEncontrado() {
        when(repository.findById("2"))
                .thenReturn(Mono.empty());


        Mono<TipoPrestamo> result = adapter.findById(2);

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();

        verify(repository).findById("2");
        verify(mapper, times(0)).toDomain(any());
    }

    @Test
    void buscarPorId_debePropagarError_cuandoElRepositorioFalla() {
        RuntimeException expectedException = new RuntimeException("Error en la base de datos");
        when(repository.findById("3"))
                .thenReturn(Mono.error(expectedException));

        Mono<TipoPrestamo> result = adapter.findById(3);
        
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Error en la base de datos"))
                .verify();

        verify(repository).findById("3");
        verify(mapper, times(0)).toDomain(any());
    }
}
