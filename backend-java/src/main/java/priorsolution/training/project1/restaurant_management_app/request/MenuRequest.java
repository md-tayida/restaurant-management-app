package priorsolution.training.project1.restaurant_management_app.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import priorsolution.training.project1.restaurant_management_app.entity.enums.MenuStatusEnum;

import java.math.BigDecimal;
@Getter
@Setter
public class MenuRequest {

    private String name;

    private BigDecimal price;

    private String imgUrl;

    private Long categoryId;

    private MenuStatusEnum status;
}
