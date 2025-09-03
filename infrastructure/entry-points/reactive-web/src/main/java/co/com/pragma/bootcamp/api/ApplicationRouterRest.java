package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.dto.ApplicationRequest;
import co.com.pragma.bootcamp.api.dto.ApplicationResponse;
import co.com.pragma.bootcamp.api.dto.ApplicationSummaryResponse;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
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
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
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


    @Bean
    @RouterOperation(
            path = BASE_PATH,
            produces = {"application/json"},
            method = RequestMethod.GET,
            beanClass = ApplicationHandler.class,
            beanMethod = "list",
            operation = @Operation(
                    operationId = "listApplicationsForReview",
                    summary = "List applications for manual review",
                    description = "Retrieves a paginated and filterable list of applications that require manual review by an advisor.",
                    tags = {"Applications"},
                    parameters = {
                            @Parameter(name = "page", description = "Page number (0-indexed)", required = false, schema = @Schema(type = "integer", defaultValue = "0")),
                            @Parameter(name = "size", description = "Number of elements per page", required = false, schema = @Schema(type = "integer", defaultValue = "10")),
                            @Parameter(name = "stateName", description = "Filter by application state (e.g., PENDING_REVIEW, REJECTED)", required = false, schema = @Schema(type = "string", defaultValue = "PENDING_REVIEW"))
                    },
                    responses = {
                            @ApiResponse(responseCode = "200", description = "List of applications for review",
                                    content = @Content(schema = @Schema(implementation = ApplicationSummaryResponse.class))),
                            @ApiResponse(responseCode = "400", description = "Invalid request or state not found"),
                            @ApiResponse(responseCode = "403", description = "Forbidden (user not an Advisor)"),
                            @ApiResponse(responseCode = "500", description = "Internal Server Error")
                    },
                    security = @SecurityRequirement(name = "bearerAuth")
            )
    )
    public RouterFunction<ServerResponse> listApplicationsForReviewRoute(ApplicationHandler applicationHandler) {
        return route(GET(BASE_PATH), applicationHandler::list);
    }
}
