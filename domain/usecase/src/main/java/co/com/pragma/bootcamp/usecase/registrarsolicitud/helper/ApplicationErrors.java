package co.com.pragma.bootcamp.usecase.registrarsolicitud.helper;

public enum ApplicationErrors {
    LOAN_TYPE_DOES_NOT_EXIST("Loan type does not exist"),
    AMOUNT_OUT_OF_RANGE("Amount is out of range for the loan type"),
    CLIENT_NOT_FOUND("Client not found");

    private final String message;

    ApplicationErrors(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
