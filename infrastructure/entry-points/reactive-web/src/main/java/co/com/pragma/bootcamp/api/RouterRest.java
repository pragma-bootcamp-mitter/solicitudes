package co.com.pragma.bootcamp.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterRest {
    private static final String BASE = "/api/v1/solicitud";

    @Bean
    public RouterFunction<ServerResponse> routes(Handler handler) {
        return RouterFunctions
                .route(RequestPredicates.POST(BASE), handler::registrar)
                .andRoute(RequestPredicates.GET(BASE), handler::listar);
    }
}
