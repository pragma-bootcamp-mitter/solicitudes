package co.com.pragma.bootcamp.model.exceptions;

public enum BusinessErrorCode {
    BR_400_BAD_REQUEST("BR_400_BAD_REQUEST", "Bad request from client"),
    BR_409_CONFLICT("BR_409_CONFLICT", "Conflict with existing data"),
    BR_404_NOT_FOUND("BR_404_NOT_FOUND", "Resource not found"),

    BR_500_INTERNAL_SERVER_ERROR("BR_500_INTERNAL_SERVER_ERROR", "An unexpected error occurred");

    private final String code;
    private final String defaultMessage;

    BusinessErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}