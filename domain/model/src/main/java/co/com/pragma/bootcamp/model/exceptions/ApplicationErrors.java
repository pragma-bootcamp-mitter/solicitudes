package co.com.pragma.bootcamp.model.exceptions;

public enum ApplicationErrors {
    LOAN_TYPE_DOES_NOT_EXIST(BusinessErrorCode.BR_404_NOT_FOUND, "Loan type does not exist"),
    AMOUNT_OUT_OF_RANGE(BusinessErrorCode.BR_400_BAD_REQUEST, "Amount is out of range for the loan type"),
    CLIENT_NOT_FOUND(BusinessErrorCode.BR_404_NOT_FOUND, "Client not found"),
    STATE_NOT_FOUND(BusinessErrorCode.BR_404_NOT_FOUND, "State not found"),
    UNAUTHORIZED_OPERATION(BusinessErrorCode.BR_403_FORBIDDEN, "Unauthorized operation");
    ;

    private final BusinessErrorCode errorCode;
    private final String message;

    ApplicationErrors(BusinessErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public BusinessErrorCode getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}