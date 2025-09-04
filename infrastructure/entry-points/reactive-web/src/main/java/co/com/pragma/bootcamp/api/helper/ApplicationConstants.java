package co.com.pragma.bootcamp.api.helper;

public final class ApplicationConstants {

    private ApplicationConstants() {}

    public static final String SUCCESS_CODE = "B200-000";
    public static final String VALIDATION_ERROR_CODE = "B400-000";
    public static final String INTERNAL_SERVER_ERROR_CODE = "B500-001";
    public static final String UNAUTHORIZED_CODE = "A401-001";
    public static final String FORBIDDEN_CODE = "A403-001";

    public static final String SUCCESS_TITLE = "Successfully";
    public static final String BAD_REQUEST_TITLE = "Bad Request";
    public static final String INTERNAL_SERVER_ERROR_TITLE = "Internal Server Error";
    public static final String UNAUTHORIZED_TITLE = "Unauthorized";
    public static final String FORBIDDEN_TITLE = "Forbidden";

    public static final String SUCCESS_MESSAGE = "Operation carried out successfully";
    public static final String VALIDATION_ERROR_MESSAGE = "Bad Request-fields bad format";
    public static final String UNAUTHORIZED_MESSAGE = "Authentication credentials not provided or invalid.";
    public static final String FORBIDDEN_MESSAGE = "You do not have permission to access this resource.";

    public static final String SERIALIZATION_ERROR_MESSAGE = "Error serializing error response";
    public static final String GENERIC_ERROR_MESSAGE = "An unexpected error has occurred";
    public static final String UNEXPECTED_ERROR_MESSAGE = "Unexpected error during request processing";
    public static final String FIELD_KEY = "field";
    public static final String ERROR_KEY = "error";
}