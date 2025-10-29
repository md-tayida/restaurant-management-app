package priorsolution.training.project1.restaurant_management_app.Response;

import lombok.Getter;
import lombok.Setter;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderStatusEnum;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
public class OrderResponse {
    private Long id;
    private OrderTypeEnum orderType;
    private OrderStatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private BigDecimal totalPrice;
    private Long tableId;
    private List<OrderItemResponse> orderItems;

}
