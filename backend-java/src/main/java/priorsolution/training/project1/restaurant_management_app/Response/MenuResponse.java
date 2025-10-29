package priorsolution.training.project1.restaurant_management_app.Response;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import priorsolution.training.project1.restaurant_management_app.entity.enums.MenuStatusEnum;

import java.math.BigDecimal;
@Getter
@Setter
public class MenuResponse {
    private Long id;
    @NotBlank(message = "Menu name is required")
    private String name;
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
    private BigDecimal price;
    private String imgUrl;
    private MenuStatusEnum status;
    private MenuCategoryResponse category;
}
