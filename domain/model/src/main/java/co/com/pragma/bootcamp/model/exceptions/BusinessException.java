package co.com.pragma.bootcamp.model.exceptions;

import lombok.Getter;
import java.util.List;

@Getter
public class BusinessException extends RuntimeException {
    private final ApplicationErrors applicationError;
    private final List<String> messages;

    public BusinessException(ApplicationErrors applicationError, List<String> messages) {
        super(applicationError.getMessage());
        this.applicationError = applicationError;
        this.messages = messages;
    }

    public BusinessException(ApplicationErrors applicationError) {
        this(applicationError, List.of(applicationError.getMessage()));
    }
}