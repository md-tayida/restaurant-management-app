package priorsolution.training.project1.restaurant_management_app.request;

import lombok.Getter;
import lombok.Setter;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderStatusEnum;

@Getter
@Setter
public class OrderStatusRequest {
    private OrderStatusEnum orderStatus;
}
