package co.com.pragma.bootcamp.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

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

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code("B200-000")
                .message("Operation carried out successfully")
                .title("Successfully")
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
                .code("B400-000")
                .message("Bad Request-fields bad format")
                .title("Bad Request")
                .errors(errors)
                .build();
    }
}