package co.com.pragma.bootcamp.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

import static co.com.pragma.bootcamp.api.helper.ApplicationConstants.BAD_REQUEST_TITLE;
import static co.com.pragma.bootcamp.api.helper.ApplicationConstants.SUCCESS_CODE;
import static co.com.pragma.bootcamp.api.helper.ApplicationConstants.SUCCESS_MESSAGE;
import static co.com.pragma.bootcamp.api.helper.ApplicationConstants.SUCCESS_TITLE;
import static co.com.pragma.bootcamp.api.helper.ApplicationConstants.VALIDATION_ERROR_CODE;
import static co.com.pragma.bootcamp.api.helper.ApplicationConstants.VALIDATION_ERROR_MESSAGE;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private String code;
    private String message;
    private String title;
    private T data;
    private List<Map<String, String>> errors;

    private Integer page;
    private Integer size;
    private Long totalElements;
    private Integer totalPages;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(SUCCESS_CODE)
                .message(SUCCESS_MESSAGE)
                .title(SUCCESS_TITLE)
                .data(data)
                .build();
    }

    public static ApiResponse<Void> businessError(String code, String message, String title) {
        return ApiResponse.<Void>builder()
                .code(code)
                .message(message)
                .title(title)
                .build();
    }

    public static ApiResponse<Void> validationError(List<Map<String, String>> errors) {
        return ApiResponse.<Void>builder()
                .code(VALIDATION_ERROR_CODE)
                .message(VALIDATION_ERROR_MESSAGE)
                .title(BAD_REQUEST_TITLE)
                .errors(errors)
                .build();
    }

    public static <T> ApiResponse<T> success(T data, int page, int size, long totalElements, int totalPages) {
        return ApiResponse.<T>builder()
                .code(SUCCESS_CODE)
                .message(SUCCESS_MESSAGE)
                .title(SUCCESS_TITLE)
                .data(data)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();
    }
}