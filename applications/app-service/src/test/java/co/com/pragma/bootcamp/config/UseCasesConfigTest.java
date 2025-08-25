package co.com.pragma.bootcamp.config;

import co.com.pragma.bootcamp.model.solicitud.Solicitud;
import co.com.pragma.bootcamp.model.solicitud.gateways.SolicitudRepository;
import co.com.pragma.bootcamp.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.pragma.bootcamp.model.user.gateways.AuthRepository;
import co.com.pragma.bootcamp.usecase.registrarsolicitud.RegistrarSolicitudUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            String[] beanNames = context.getBeanDefinitionNames();

            boolean useCaseBeanFound = false;
            for (String beanName : beanNames) {
                if (beanName.endsWith("UseCase")) {
                    useCaseBeanFound = true;
                    break;
                }
            }

            assertTrue(useCaseBeanFound, "No beans ending with 'Use Case' were found");
        }
    }

    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig {

        @Mock
        private SolicitudRepository solicitudRepository;
        @Mock
        private TipoPrestamoRepository tipoPrestamoRepository;
        @Mock
        private AuthRepository authRepository;
        @Mock
        private Function<Mono<Solicitud>, Mono<Solicitud>> transactionalWrapper;

        @Bean
        public MyUseCase myUseCase() {
            return new MyUseCase();
        }

        @Bean
        public RegistrarSolicitudUseCase registrarSolicitudUseCase() {
            return new RegistrarSolicitudUseCase(
                    solicitudRepository,
                    tipoPrestamoRepository,
                    authRepository,
                    transactionalWrapper
            );
        }


    }

    static class MyUseCase {
        public String execute() {
            return "MyUseCase Test";
        }
    }
}