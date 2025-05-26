package priorsolution.training.project1.restaurant_management_app.dto;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderItemRequestDTO {
    @NotNull(message = "Menu ID must not be null")
    private Long menuId;

    @Positive(message = "Quantity must be greater than zero")
    private int quantity;

    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be zero or positive")
    private BigDecimal price;


}
