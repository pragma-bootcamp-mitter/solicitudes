package co.com.pragma.bootcamp.api.config;

import co.com.pragma.bootcamp.api.dto.ApiResponse;
import co.com.pragma.bootcamp.model.exceptions.BusinessErrorCode;
import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;

import static co.com.pragma.bootcamp.api.helper.ApplicationConstants.ERROR_KEY;
import static co.com.pragma.bootcamp.api.helper.ApplicationConstants.FIELD_KEY;
import static co.com.pragma.bootcamp.api.helper.ApplicationConstants.GENERIC_ERROR_MESSAGE;
import static co.com.pragma.bootcamp.api.helper.ApplicationConstants.INTERNAL_SERVER_ERROR_CODE;
import static co.com.pragma.bootcamp.api.helper.ApplicationConstants.INTERNAL_SERVER_ERROR_TITLE;
import static co.com.pragma.bootcamp.api.helper.ApplicationConstants.SERIALIZATION_ERROR_MESSAGE;
import static co.com.pragma.bootcamp.api.helper.ApplicationConstants.UNEXPECTED_ERROR_MESSAGE;
import static java.util.stream.Collectors.toList;

@Component
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error("Handling exception: {}", ex.getMessage());

        return switch (ex) {
            case BusinessException businessEx -> {
                BusinessErrorCode errorCode = businessEx.getApplicationError().getErrorCode();
                HttpStatus status = mapBusinessErrorCodeToHttpStatus(errorCode);
                ApiResponse<?> apiResponse = ApiResponse.businessError(
                        errorCode.getCode(),
                        businessEx.getApplicationError().getMessage(),
                        errorCode.getDefaultMessage()
                );
                yield buildErrorResponse(exchange, status, apiResponse);
            }
            case WebExchangeBindException validationEx -> {
                HttpStatus status = HttpStatus.BAD_REQUEST;
                List<Map<String, String>> errors = validationEx.getFieldErrors().stream()
                        .map(e -> Map.of(FIELD_KEY, e.getField(), ERROR_KEY, e.getDefaultMessage()))
                        .toList();
                ApiResponse<?> apiResponse = ApiResponse.validationError(errors);
                yield buildErrorResponse(exchange, status, apiResponse);
            }
            case ConstraintViolationException constraintViolationEx -> {
                HttpStatus status = HttpStatus.BAD_REQUEST;
                List<Map<String, String>> errors = constraintViolationEx.getConstraintViolations().stream()
                        .map(violation -> Map.of(FIELD_KEY, violation.getPropertyPath().toString(), ERROR_KEY, violation.getMessage()))
                        .collect(toList());
                ApiResponse<?> apiResponse = ApiResponse.validationError(errors);
                yield buildErrorResponse(exchange, status, apiResponse);
            }
            default -> {
                HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
                ApiResponse<?> apiResponse = ApiResponse.businessError(
                        INTERNAL_SERVER_ERROR_CODE,
                        GENERIC_ERROR_MESSAGE,
                        INTERNAL_SERVER_ERROR_TITLE
                );
                log.error(UNEXPECTED_ERROR_MESSAGE, ex);
                yield buildErrorResponse(exchange, status, apiResponse);
            }
        };
    }

    private Mono<Void> buildErrorResponse(ServerWebExchange exchange, HttpStatus status, ApiResponse<?> apiResponse) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(apiResponse);
            var buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error(SERIALIZATION_ERROR_MESSAGE, e);
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return exchange.getResponse().setComplete();
        }
    }

    private HttpStatus mapBusinessErrorCodeToHttpStatus(BusinessErrorCode errorCode) {
        return switch (errorCode) {
            case BR_409_CONFLICT -> HttpStatus.CONFLICT;
            case BR_404_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case BR_400_BAD_REQUEST -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}