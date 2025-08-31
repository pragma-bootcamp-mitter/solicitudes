package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.dto.ApplicationRequest;
import co.com.pragma.bootcamp.api.dto.ApplicationResponse;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Loan Applications API",
                version = "1.0.0",
                description = "API for managing loan applications in the system",
                contact = @Contact(name = "Bootcamp Team", email = "soporte@pragma.com")
        )
)
public class ApplicationRouterRest {
    private static final String BASE_PATH = "/api/v1/applications";

    @Bean
    @RouterOperation(
            path = BASE_PATH,
            produces = {"application/json"},
            method = RequestMethod.POST,
            beanClass = ApplicationHandler.class,
            beanMethod = "register",
            operation = @Operation(
                    operationId = "registerApplication",
                    summary = "Register a new application",
                    description = "Creates a new loan application in the system",
                    tags = {"Applications"},
                    requestBody = @RequestBody(
                            required = true,
                            content = @Content(schema = @Schema(implementation = ApplicationRequest.class))
                    ),
                    responses = {
                            @ApiResponse(responseCode = "201", description = "Application created",
                                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
                            @ApiResponse(responseCode = "400", description = "Invalid application"),
                            @ApiResponse(responseCode = "500", description = "Internal Server Error")
                    }
            )
    )
    public RouterFunction<ServerResponse> registerApplicationRoute(ApplicationHandler applicationHandler) {
        return route(POST(BASE_PATH), applicationHandler::register);
    }

    @Bean
    @RouterOperation(
            path = BASE_PATH,
            produces = {"application/json"},
            method = RequestMethod.GET,
            beanClass = ApplicationHandler.class,
            beanMethod = "list",
            operation = @Operation(
                    operationId = "listApplications",
                    summary = "List all applications",
                    description = "Retrieves all registered applications",
                    tags = {"Applications"},
                    responses = {
                            @ApiResponse(responseCode = "200", description = "List of applications",
                                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
                            @ApiResponse(responseCode = "204", description = "No applications registered"),
                            @ApiResponse(responseCode = "500", description = "Internal Server Error")
                    }
            )
    )
    public RouterFunction<ServerResponse> listApplicationsRoute(ApplicationHandler applicationHandler) {
        return route(GET(BASE_PATH), applicationHandler::list);
    }
}
