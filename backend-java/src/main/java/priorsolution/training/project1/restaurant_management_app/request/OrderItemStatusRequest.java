package priorsolution.training.project1.restaurant_management_app.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderItemStatusEnum;

@Getter
@Setter
public class OrderItemStatusRequest {
    @NotNull
    private OrderItemStatusEnum orderItemStatus;

}
