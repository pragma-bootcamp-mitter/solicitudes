package co.com.pragma.bootcamp.api.helper;

import co.com.pragma.bootcamp.api.dto.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;

import static co.com.pragma.bootcamp.api.helper.ApplicationConstants.SUCCESS_CODE;
import static co.com.pragma.bootcamp.api.helper.ApplicationConstants.SUCCESS_MESSAGE;
import static co.com.pragma.bootcamp.api.helper.ApplicationConstants.SUCCESS_TITLE;
import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    private static class SampleData {
        public String value = "test";
    }

    @Test
    void testSuccessWithPaginationMethod() {
        SampleData sampleData = new SampleData();
        int page = 1;
        int size = 10;
        long totalElements = 25;
        int totalPages = 5;

        ApiResponse<SampleData> response = ApiResponse.success(sampleData, page, size, totalElements, totalPages);

        assertThat(response.getCode()).isEqualTo(SUCCESS_CODE);
        assertThat(response.getMessage()).isEqualTo(SUCCESS_MESSAGE);
        assertThat(response.getTitle()).isEqualTo(SUCCESS_TITLE);
        assertThat(response.getData()).isEqualTo(sampleData);
        assertThat(response.getErrors()).isNull();

        // Assertions para los nuevos campos de paginación
        assertThat(response.getPage()).isEqualTo(page);
        assertThat(response.getSize()).isEqualTo(size);
        assertThat(response.getTotalElements()).isEqualTo(totalElements);
    }

    @Test
    void testBuilderConstructor() {
        String code = "B100";
        String message = "Mensaje de prueba";
        String title = "Título de prueba";
        SampleData data = new SampleData();
        List<Map<String, String>> errors = List.of(Map.of("field", "email", "defaultMessage", "email inválido"));

        ApiResponse<SampleData> apiResponse = ApiResponse.<SampleData>builder()
                .code(code)
                .message(message)
                .title(title)
                .data(data)
                .errors(errors)
                .build();

        assertThat(apiResponse.getCode()).isEqualTo(code);
        assertThat(apiResponse.getMessage()).isEqualTo(message);
        assertThat(apiResponse.getTitle()).isEqualTo(title);
        assertThat(apiResponse.getData()).isEqualTo(data);
        assertThat(apiResponse.getErrors()).isEqualTo(errors);
    }


    @Test
    void testAllArgsConstructor() {
        String code = "B100";
        String message = "Test message";
        String title = "Test title";
        SampleData data = new SampleData();
        List<Map<String, String>> errors = List.of(Map.of("field", "email", "defaultMessage", "Invalid email"));

        Integer page = 1;
        Integer size = 10;
        Long totalElements = 50L;
        Integer totalPages = 4;

        ApiResponse<SampleData> apiResponse = new ApiResponse<>(code, message, title, data, errors, page, size, totalElements, totalPages);

        assertThat(apiResponse.getCode()).isEqualTo(code);
        assertThat(apiResponse.getMessage()).isEqualTo(message);
        assertThat(apiResponse.getTitle()).isEqualTo(title);
        assertThat(apiResponse.getData()).isEqualTo(data);
        assertThat(apiResponse.getErrors()).isEqualTo(errors);

        assertThat(apiResponse.getPage()).isEqualTo(page);
        assertThat(apiResponse.getSize()).isEqualTo(size);
        assertThat(apiResponse.getTotalElements()).isEqualTo(totalElements);
    }

    @Test
    void testNoArgsConstructor() {
        ApiResponse<Void> apiResponse = new ApiResponse<>();

        assertThat(apiResponse).isNotNull();
        assertThat(apiResponse.getCode()).isNull();
        assertThat(apiResponse.getMessage()).isNull();
        assertThat(apiResponse.getTitle()).isNull();
        assertThat(apiResponse.getData()).isNull();
        assertThat(apiResponse.getErrors()).isNull();
    }

    @Test
    void testSuccessMethod() {
        SampleData sampleData = new SampleData();

        ApiResponse<SampleData> response = ApiResponse.success(sampleData);

        assertThat(response.getCode()).isEqualTo("B200-000");
        assertThat(response.getMessage()).isEqualTo("Operation carried out successfully");
        assertThat(response.getTitle()).isEqualTo("Successfully");
        assertThat(response.getData()).isEqualTo(sampleData);
        assertThat(response.getErrors()).isNull();
    }

    @Test
    void testBusinessErrorMethod() {
        String code = "B404-001";
        String message = "Recurso no encontrado";
        String title = "No encontrado";

        ApiResponse<Void> response = ApiResponse.businessError(code, message, title);

        assertThat(response.getCode()).isEqualTo(code);
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.getTitle()).isEqualTo(title);
        assertThat(response.getData()).isNull();
        assertThat(response.getErrors()).isNull();
    }

    @Test
    void testValidationErrorMethod() {
        List<Map<String, String>> errors = List.of(
                Map.of("field", "name", "defaultMessage", "El nombre es obligatorio"),
                Map.of("field", "age", "defaultMessage", "La edad debe ser un número")
        );

        ApiResponse<Void> response = ApiResponse.validationError(errors);

        assertThat(response.getCode()).isEqualTo("B400-000");
        assertThat(response.getMessage()).isEqualTo("Bad Request-fields bad format");
        assertThat(response.getTitle()).isEqualTo("Bad Request");
        assertThat(response.getData()).isNull();
        assertThat(response.getErrors()).isEqualTo(errors);
    }

    @Test
    void testJsonSerializationSuccess() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        SampleData sampleData = new SampleData();
        ApiResponse<SampleData> response = ApiResponse.success(sampleData);

        String json = mapper.writeValueAsString(response);

        System.out.println(json);
        assertThat(json).contains("\"code\":\"B200-000\"");
        assertThat(json).contains("\"message\":\"Operation carried out successfully\"");
        assertThat(json).contains("\"title\":\"Successfully\"");
        assertThat(json).contains("\"data\":{\"value\":\"test\"}");
        assertThat(json).doesNotContain("errors");
    }
}