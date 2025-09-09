package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.r2dbc.webclient.AuthClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AuthClientTest {

    @Mock
    private ExchangeFunction exchangeFunction;
    private AuthClient authClient;
    private WebClient webClient;

    @BeforeEach
    void setUp() {
        webClient = WebClient.builder().exchangeFunction(exchangeFunction).build();
        authClient = new AuthClient(webClient);
    }

    @Test
    void getUserByDocument_successfulResponse_returnsUserAuth() {
        String responseBody = "{\"code\":\"200\",\"message\":\"OK\",\"title\":\"Success\",\"data\":{\"id\":\"1\",\"identificationDocument\":\"12345\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"dateOfBirth\":\"1990-01-01\",\"address\":\"Street 123\",\"phoneNumber\":\"555-555\",\"email\":\"test@email.com\",\"baseSalary\":5000,\"password\":\"pass123\",\"roleId\":1}}";

        ClientResponse clientResponse = ClientResponse.create(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(responseBody)
                .build();

        when(exchangeFunction.exchange(any())).thenReturn(Mono.just(clientResponse));

        StepVerifier.create(authClient.getUserByDocument("12345"))
                .expectNextMatches(user ->
                        user.getIdentificationDocument().equals("12345") &&
                                user.getFirstName().equals("John"))
                .verifyComplete();
    }

    @Test
    void getUserByDocument_clientError_throwsBusinessException() {
        ClientResponse clientResponse = ClientResponse.create(HttpStatus.NOT_FOUND).build();
        when(exchangeFunction.exchange(any())).thenReturn(Mono.just(clientResponse));
        StepVerifier.create(authClient.getUserByDocument("99999"))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void getUserByDocument_serverError_throwsException() {
        ClientResponse clientResponse = ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(exchangeFunction.exchange(any())).thenReturn(Mono.just(clientResponse));

        StepVerifier.create(authClient.getUserByDocument("12345"))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void getUserByDocument_webClientException_mapsToRuntimeException() {
        when(exchangeFunction.exchange(any()))
                .thenReturn(Mono.error(WebClientResponseException.create(
                        500, "Server Error", null, null, null)));

        StepVerifier.create(authClient.getUserByDocument("12345"))
                .expectError(RuntimeException.class)
                .verify();
    }
}
