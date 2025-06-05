package priorsolution.training.project1.restaurant_management_app.dto;

import lombok.Builder;
import lombok.Data;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderItemStatusEnum;

import java.time.LocalDateTime;

@Data
@Builder

public class OrderItemStatusUpdateDTO  {
    private Long id;
    private LocalDateTime createTime;
    private String tableNumber;
    private String menuName;
    private int quantity;
    private OrderItemStatusEnum status;

}
