package priorsolution.training.project1.restaurant_management_app.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class AuthRequestDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validate_whenAuthRequestDTOIsValid_shouldHaveNoViolations() {
        AuthRequestDTO dto = new AuthRequestDTO("user123", "pass123");
        Set<ConstraintViolation<AuthRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }

    @Test
    void validate_whenUsernameIsNull_shouldFailValidation() {
        AuthRequestDTO dto = new AuthRequestDTO(null, "pass123");
        Set<ConstraintViolation<AuthRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    void validate_whenUsernameIsBlank_shouldFailValidation() {
        AuthRequestDTO dto = new AuthRequestDTO("   ", "pass123");
        Set<ConstraintViolation<AuthRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    void validate_whenPasswordIsNull_shouldFailValidation() {
        AuthRequestDTO dto = new AuthRequestDTO("user123", null);
        Set<ConstraintViolation<AuthRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    void validate_whenPasswordIsBlank_shouldFailValidation() {
        AuthRequestDTO dto = new AuthRequestDTO("user123", "");
        Set<ConstraintViolation<AuthRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }
}
