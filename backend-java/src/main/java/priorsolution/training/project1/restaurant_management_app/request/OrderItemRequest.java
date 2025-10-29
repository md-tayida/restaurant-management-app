package priorsolution.training.project1.restaurant_management_app.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class OrderItemRequest {
    @NotNull(message = "Menu ID must not be null")
    private Long menuId;

    @Positive(message = "Quantity must be greater than zero")
    private int quantity;

    private String description;
}
