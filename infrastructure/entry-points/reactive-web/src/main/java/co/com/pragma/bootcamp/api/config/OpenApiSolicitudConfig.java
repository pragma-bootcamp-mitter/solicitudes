package co.com.pragma.bootcamp.api.config;

import co.com.pragma.bootcamp.api.Handler;
import co.com.pragma.bootcamp.api.dto.ApplicationRequest;
import co.com.pragma.bootcamp.api.dto.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class OpenApiSolicitudConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ApplicationMapper API")
                        .version("1.0.0")
                        .description("API para gestionar solicitudes de préstamos")
                        .contact(new Contact()
                                .name("Equipo Bootcamp")
                                .email("soporte@pragma.com")
                        )
                );
    }

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    produces = {"application/json"},
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "register",
                    operation = @Operation(
                            operationId = "registrarSolicitud",
                            summary = "Registrar una nueva solicitud",
                            description = "Crea una nueva solicitud de préstamo en el sistema",
                            tags = {"Solicitudes"},
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = ApplicationRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "ApplicationMapper creada",
                                            content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "ApplicationMapper inválida"),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    produces = {"application/json"},
                    method = RequestMethod.GET,
                    beanClass = Handler.class,
                    beanMethod = "list",
                    operation = @Operation(
                            operationId = "listarSolicitudes",
                            summary = "Listar todas las solicitudes",
                            description = "Obtiene todas las solicitudes registradas",
                            tags = {"Solicitudes"},
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Listado de solicitudes",
                                            content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
                                    @ApiResponse(responseCode = "204", description = "No hay solicitudes registradas"),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> router(Handler handler) {
        return RouterFunctions.route()
                .POST("/api/v1/solicitud", handler::register)
                .GET("/api/v1/solicitud", handler::list)
                .build();
    }
}
