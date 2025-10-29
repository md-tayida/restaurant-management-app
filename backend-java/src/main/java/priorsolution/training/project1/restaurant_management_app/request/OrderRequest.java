package priorsolution.training.project1.restaurant_management_app.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderTypeEnum;

import java.util.List;
@Getter
@Setter
public class OrderRequest {
    @NotNull
    private OrderTypeEnum orderType;
    private Long tableId;
    @NotNull

    @Size(min = 1, message = "At least one item is required")
    private List<OrderItemRequest> orderItems;
}
