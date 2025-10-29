package priorsolution.training.project1.restaurant_management_app.Response;

import lombok.Getter;
import lombok.Setter;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderItemStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class OrderItemResponse {
    private Long id;
    private Long menuId;
    private String menuName;
    private Integer quantity;
    private BigDecimal price;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private OrderItemStatusEnum status;
}
