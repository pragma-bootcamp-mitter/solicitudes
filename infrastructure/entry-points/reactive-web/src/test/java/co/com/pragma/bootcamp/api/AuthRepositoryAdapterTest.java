package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.dto.AuthUserDto;
import co.com.pragma.bootcamp.api.mapper.AuthUserMapper;
import co.com.pragma.bootcamp.api.webclient.AuthClient;
import co.com.pragma.bootcamp.model.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthRepositoryAdapterTest {

    @InjectMocks
    private AuthRepositoryAdapter repositoryAdapter;

    @Mock
    private AuthClient client;

    @Mock
    private AuthUserMapper mapper;

    @Test
    void getUserByDocumento_Existente_DeberiaRetornarUsuario() {
        String documento = "123456";
        AuthUserDto dto = new AuthUserDto();
        dto.setId("u1");
        dto.setDocumentoIdentidad(documento);
        dto.setNombres("Juan");
        dto.setApellidos("Perez");

        User userDomain = User.builder()
                .id("u1")
                .documentoIdentidad(documento)
                .nombres("Juan")
                .apellidos("Perez")
                .build();

        when(client.getUserByDocumento(documento)).thenReturn(Mono.just(dto));
        when(mapper.toDomain(dto)).thenReturn(userDomain);

        Mono<User> result = repositoryAdapter.getUserByDocumento(documento);

        StepVerifier.create(result)
                .expectNextMatches(user ->
                        user.getId().equals("u1") &&
                                user.getDocumentoIdentidad().equals(documento) &&
                                user.getNombres().equals("Juan") &&
                                user.getApellidos().equals("Perez")
                )
                .verifyComplete();
    }

    @Test
    void getUserByDocumento_NoExistente_DeberiaRetornarVacio() {
        String documento = "999999";
        when(client.getUserByDocumento(documento)).thenReturn(Mono.empty());

        Mono<User> result = repositoryAdapter.getUserByDocumento(documento);

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }
}
