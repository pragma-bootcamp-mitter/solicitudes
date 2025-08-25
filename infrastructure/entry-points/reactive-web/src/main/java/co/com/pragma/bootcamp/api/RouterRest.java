package co.com.pragma.bootcamp.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    private static final String BASE = "/api/v1/solicitud";

    @Bean
    public org.springframework.web.reactive.function.server.RouterFunction<ServerResponse> routes(
            /*SolicitudHandler handler*/
            Handler handler

    ) {
        return RouterFunctions.route()
                .POST(BASE, handler::registrar)
                .build();
    }
}
