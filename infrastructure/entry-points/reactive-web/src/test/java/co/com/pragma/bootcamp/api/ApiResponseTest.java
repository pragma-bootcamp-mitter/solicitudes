package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.dto.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests para la clase ApiResponse")
class ApiResponseTest {

    // Simula una clase de datos de ejemplo para las pruebas
    private static class SampleData {
        public String value = "test";
    }

    @Test
    @DisplayName("Debe construir un objeto con el constructor @Builder")
    void testBuilderConstructor() {
        // Arrange
        String code = "B100";
        String message = "Mensaje de prueba";
        String title = "Título de prueba";
        SampleData data = new SampleData();
        List<Map<String, String>> errors = List.of(Map.of("field", "email", "defaultMessage", "email inválido"));

        // Act
        ApiResponse<SampleData> apiResponse = ApiResponse.<SampleData>builder()
                .code(code)
                .message(message)
                .title(title)
                .data(data)
                .errors(errors)
                .build();

        // Assert
        assertThat(apiResponse.getCode()).isEqualTo(code);
        assertThat(apiResponse.getMessage()).isEqualTo(message);
        assertThat(apiResponse.getTitle()).isEqualTo(title);
        assertThat(apiResponse.getData()).isEqualTo(data);
        assertThat(apiResponse.getErrors()).isEqualTo(errors);
    }

    @Test
    @DisplayName("Debe construir un objeto con el constructor @AllArgsConstructor")
    void testAllArgsConstructor() {
        // Arrange
        String code = "B100";
        String message = "Mensaje de prueba";
        String title = "Título de prueba";
        SampleData data = new SampleData();
        List<Map<String, String>> errors = List.of(Map.of("field", "email", "defaultMessage", "email inválido"));

        // Act
        ApiResponse<SampleData> apiResponse = new ApiResponse<>(code, message, title, data, errors);

        // Assert
        assertThat(apiResponse.getCode()).isEqualTo(code);
        assertThat(apiResponse.getMessage()).isEqualTo(message);
        assertThat(apiResponse.getTitle()).isEqualTo(title);
        assertThat(apiResponse.getData()).isEqualTo(data);
        assertThat(apiResponse.getErrors()).isEqualTo(errors);
    }

    @Test
    @DisplayName("Debe construir un objeto con el constructor @NoArgsConstructor")
    void testNoArgsConstructor() {
        // Act
        ApiResponse<Void> apiResponse = new ApiResponse<>();

        // Assert
        assertThat(apiResponse).isNotNull();
        assertThat(apiResponse.getCode()).isNull();
        assertThat(apiResponse.getMessage()).isNull();
        assertThat(apiResponse.getTitle()).isNull();
        assertThat(apiResponse.getData()).isNull();
        assertThat(apiResponse.getErrors()).isNull();
    }

    @Test
    @DisplayName("Debe crear un ApiResponse de éxito con el método success()")
    void testSuccessMethod() {
        // Arrange
        SampleData sampleData = new SampleData();

        // Act
        ApiResponse<SampleData> response = ApiResponse.success(sampleData);

        // Assert
        assertThat(response.getCode()).isEqualTo("B200-000");
        assertThat(response.getMessage()).isEqualTo("Operation carried out successfully");
        assertThat(response.getTitle()).isEqualTo("Successfully");
        assertThat(response.getData()).isEqualTo(sampleData);
        assertThat(response.getErrors()).isNull();
    }

    @Test
    @DisplayName("Debe crear un ApiResponse de error de negocio con el método businessError()")
    void testBusinessErrorMethod() {
        // Arrange
        String code = "B404-001";
        String message = "Recurso no encontrado";
        String title = "No encontrado";

        // Act
        ApiResponse<Void> response = ApiResponse.businessError(code, message, title);

        // Assert
        assertThat(response.getCode()).isEqualTo(code);
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.getTitle()).isEqualTo(title);
        assertThat(response.getData()).isNull();
        assertThat(response.getErrors()).isNull();
    }

    @Test
    @DisplayName("Debe crear un ApiResponse de error de validación con el método validationError()")
    void testValidationErrorMethod() {
        // Arrange
        List<Map<String, String>> errors = List.of(
                Map.of("field", "name", "defaultMessage", "El nombre es obligatorio"),
                Map.of("field", "age", "defaultMessage", "La edad debe ser un número")
        );

        // Act
        ApiResponse<Void> response = ApiResponse.validationError(errors);

        // Assert
        assertThat(response.getCode()).isEqualTo("B400-000");
        assertThat(response.getMessage()).isEqualTo("Bad Request-fields bad format");
        assertThat(response.getTitle()).isEqualTo("Bad Request");
        assertThat(response.getData()).isNull();
        assertThat(response.getErrors()).isEqualTo(errors);
    }

    @Test
    @DisplayName("Debe serializar un objeto de éxito correctamente con @JsonInclude(NON_NULL)")
    void testJsonSerializationSuccess() throws JsonProcessingException {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        SampleData sampleData = new SampleData();
        ApiResponse<SampleData> response = ApiResponse.success(sampleData);

        // Act
        String json = mapper.writeValueAsString(response);

        // Assert
        System.out.println(json); // Opcional, para ver el JSON generado
        assertThat(json).contains("\"code\":\"B200-000\"");
        assertThat(json).contains("\"message\":\"Operation carried out successfully\"");
        assertThat(json).contains("\"title\":\"Successfully\"");
        assertThat(json).contains("\"data\":{\"value\":\"test\"}");
        assertThat(json).doesNotContain("errors"); // Verifica que el campo nulo no se incluya
    }
}