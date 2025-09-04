package co.com.pragma.bootcamp.r2dbc.config;

import co.com.pragma.bootcamp.model.token.Token;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfig {

    public static final String TOKEN_KEY = "token";

    @Bean
    public WebClient authWebClient(@Value("${services.auth.base-url}") String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .filter(jwtAuthorizationFilter())
                .build();
    }

    private ExchangeFilterFunction jwtAuthorizationFilter() {
        return (request, next) -> Mono.deferContextual(contextView -> {
            if (contextView.hasKey(TOKEN_KEY)) {
                Object tokenObject = contextView.get(TOKEN_KEY);
                if (tokenObject instanceof Token) {
                    Token token = (Token) tokenObject;
                    String jwtToken = token.getAccessToken();
                    ClientRequest newRequest = ClientRequest.from(request)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                            .build();
                    return next.exchange(newRequest);
                }
            }
            return next.exchange(request);
        });
    }
}