package priorsolution.training.project1.restaurant_management_app.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MenuResponseDTO {
    private Long id;
    @NotBlank(message = "Menu name is required")
    private String name;
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
    private BigDecimal price;
    private String imgUrl;
    private MenuCategoryDTO category;
}