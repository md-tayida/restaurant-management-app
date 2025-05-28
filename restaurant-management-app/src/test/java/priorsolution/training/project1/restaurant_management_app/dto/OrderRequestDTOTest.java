package priorsolution.training.project1.restaurant_management_app.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderTypeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
public class OrderRequestDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validate_whenValidOrderRequestDTO_shouldHaveNoViolations() {
        OrderItemRequestDTO item = OrderItemRequestDTO.builder()
                .menuId(1L)
                .quantity(2)
                .build();

        OrderRequestDTO dto = OrderRequestDTO.builder()
                .orderType(OrderTypeEnum.TAKEAWAY)
                .items(List.of(item))
                .build();

        Set<ConstraintViolation<OrderRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }

    @Test
    void validate_whenOrderTypeIsNull_shouldFailValidation() {
        OrderRequestDTO dto = OrderRequestDTO.builder()
                .orderType(null)
                .items(new ArrayList<>())
                .build();

        Set<ConstraintViolation<OrderRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("orderType")));
    }

    @Test
    void validate_whenItemsIsNull_shouldFailValidation() {
        OrderRequestDTO dto = OrderRequestDTO.builder()
                .orderType(OrderTypeEnum.TAKEAWAY)
                .items(null)
                .build();

        Set<ConstraintViolation<OrderRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("items")));
    }

    @Test
    void validate_whenItemsIsEmpty_shouldFailValidation() {
        OrderRequestDTO dto = OrderRequestDTO.builder()
                .orderType(OrderTypeEnum.TAKEAWAY)
                .items(new ArrayList<>()) // empty list
                .build();

        Set<ConstraintViolation<OrderRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("items")));
    }
}
