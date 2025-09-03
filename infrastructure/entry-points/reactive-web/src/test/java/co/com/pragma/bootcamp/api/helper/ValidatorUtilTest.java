package co.com.pragma.bootcamp.api.helper;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;
import java.util.Collections;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidatorUtilTest {

    @Mock
    private Validator validator;
    private ValidatorUtil validatorUtil;

    @BeforeEach
    void setUp() {
        validatorUtil = new ValidatorUtil(validator);
    }

    @Test
    void validate_shouldReturnTarget_whenNoViolations() {
        String target = "valid";

        when(validator.validate(target)).thenReturn(Collections.emptySet());

        StepVerifier.create(validatorUtil.validate(target))
                .expectNext(target)
                .verifyComplete();

        verify(validator).validate(target);
    }

    @Test
    void validate_shouldError_whenViolationsExist() {
        String target = "invalid";

        @SuppressWarnings("unchecked")
        ConstraintViolation<String> violation = mock(ConstraintViolation.class);
        Set<ConstraintViolation<String>> violations = Set.of(violation);

        when(validator.validate(target)).thenReturn(violations);

        StepVerifier.create(validatorUtil.validate(target))
                .expectError(ConstraintViolationException.class)
                .verify();

        verify(validator).validate(target);
    }
}
