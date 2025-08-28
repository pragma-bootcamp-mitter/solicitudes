package co.com.pragma.bootcamp.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class ApplicationRouterRest {
    private static final String BASE_PATH = "/api/v1/applications";

    @Bean
    public RouterFunction<ServerResponse> routes(ApplicationHandler applicationHandler) {
        return RouterFunctions
                .route(RequestPredicates.POST(BASE_PATH).and(accept(MediaType.APPLICATION_JSON)), applicationHandler::register)
                .andRoute(RequestPredicates.GET(BASE_PATH), applicationHandler::list);
    }
}
