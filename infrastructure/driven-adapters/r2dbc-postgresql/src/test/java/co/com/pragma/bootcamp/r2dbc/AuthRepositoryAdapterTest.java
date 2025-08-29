package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.r2dbc.entity.UserAuth;
import co.com.pragma.bootcamp.r2dbc.mapper.AuthUserMapper;
import co.com.pragma.bootcamp.r2dbc.webclient.AuthClient;
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
    void getUserByDocument_shouldReturnUser_whenUserExists() {
        String document = "123456";
        UserAuth dto = new UserAuth();
        dto.setId("u1");
        dto.setIdentificationDocument(document);
        dto.setFirstName("Juan");
        dto.setLastName("Perez");

        User userDomain = User.builder()
                .id("u1")
                .identificationDocument(document)
                .firstName("Juan")
                .lastName("Perez")
                .build();

        when(client.getUserByDocument(document)).thenReturn(Mono.just(dto));
        when(mapper.toDomain(dto)).thenReturn(userDomain);

        Mono<User> result = repositoryAdapter.getUserByDocument(document);

        StepVerifier.create(result)
                .expectNextMatches(user ->
                        user.getId().equals("u1") &&
                                user.getIdentificationDocument().equals(document) &&
                                user.getFirstName().equals("Juan") &&
                                user.getLastName().equals("Perez")
                )
                .verifyComplete();
    }

    @Test
    void getUserByDocument_shouldReturnEmptyMono_whenUserDoesNotExist() {
        String document = "999999";
        when(client.getUserByDocument(document)).thenReturn(Mono.empty());

        Mono<User> result = repositoryAdapter.getUserByDocument(document);

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }
}
