package priorsolution.training.project1.restaurant_management_app.dto;

import lombok.Builder;
import lombok.Data;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderStatusEnum;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class OrderResponseDTO {
    private Long id;
    private OrderTypeEnum orderType;
    private OrderStatusEnum status;
    private LocalDateTime createdAt;
    private BigDecimal totalPrice;
    private Long tableId;
    private List<OrderItemResponseDTO> items;
}
