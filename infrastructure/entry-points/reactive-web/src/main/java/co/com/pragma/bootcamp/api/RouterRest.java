package co.com.pragma.bootcamp.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterRest {
    private static final String BASE = "/api/v1/solicitud";

    @Bean
    public org.springframework.web.reactive.function.server.RouterFunction<ServerResponse> routes(Handler handler) {
        return RouterFunctions.route()
                .POST(BASE, handler::registrar)
                .build();
    }
}
