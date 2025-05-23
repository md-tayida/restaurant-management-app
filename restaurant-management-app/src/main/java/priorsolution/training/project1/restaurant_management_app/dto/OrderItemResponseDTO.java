package priorsolution.training.project1.restaurant_management_app.dto;

import lombok.Builder;
import lombok.Data;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderItemStatusEnum;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponseDTO {
    private Long id;
    private Long menuId;
    private String menuName;
    private Integer quantity;
    private BigDecimal price;
    private OrderItemStatusEnum status;
}
