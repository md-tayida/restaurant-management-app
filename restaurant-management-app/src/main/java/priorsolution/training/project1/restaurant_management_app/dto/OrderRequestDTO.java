package priorsolution.training.project1.restaurant_management_app.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderTypeEnum;

import java.util.Collection;
import java.util.List;

@Data
public class OrderRequestDTO {
    @NotNull
    private OrderTypeEnum orderType;
    private Long tableId; // null ถ้าเป็น Takeaway
    @NotNull
    private List<OrderItemRequestDTO> items;



}
