package priorsolution.training.project1.restaurant_management_app.Response;

import lombok.Getter;
import lombok.Setter;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
public class KitchenManagementResponse {
    private Long id;
    private OrderTypeEnum orderType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BigDecimal totalPrice;
    private Long tableId;
    private List<OrderItemResponse> orderItems;
}
