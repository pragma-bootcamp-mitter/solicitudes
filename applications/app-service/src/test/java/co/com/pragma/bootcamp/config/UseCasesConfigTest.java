package co.com.pragma.bootcamp.config;

import co.com.pragma.bootcamp.model.solicitud.gateways.RepositorioSolicitud;
import co.com.pragma.bootcamp.model.tipoprestamo.gateways.RepositorioTipoPrestamo;
import co.com.pragma.bootcamp.model.user.gateways.RepositorioAuth;
import co.com.pragma.bootcamp.usecase.registrarsolicitud.RegistrarSolicitudCasoDeUso;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            String[] beanNames = context.getBeanDefinitionNames();

            boolean useCaseBeanFound = false;
            for (String beanName : beanNames) {
                if (beanName.endsWith("CasoDeUso")) {
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
        private RepositorioSolicitud repositorioSolicitud;

        @Mock
        private RepositorioTipoPrestamo repositorioTipoPrestamo;

        @Mock
        private RepositorioAuth repositorioAuth;

        @Bean
        public MiCasoDeUso miCasoDeUso() {
            return new MiCasoDeUso();
        }

        @Bean
        public RegistrarSolicitudCasoDeUso registrarSolicitudCasoDeUso() {
            return new RegistrarSolicitudCasoDeUso(
                    repositorioSolicitud,
                    repositorioTipoPrestamo,
                    repositorioAuth
            );
        }
    }

    static class MiCasoDeUso {
        public String execute() {
            return "MiCasoDeUso Test";
        }
    }
}