package priorsolution.training.project1.restaurant_management_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderStatusEnum;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private OrderStatusEnum status;
    private BigDecimal totalPrice;
    private List<OrderItemDTO> items;
}