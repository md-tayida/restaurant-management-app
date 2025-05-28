package priorsolution.training.project1.restaurant_management_app.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderTypeEnum;

import java.util.Collection;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor

public class OrderRequestDTO {
    @NotNull
    private OrderTypeEnum orderType;
    private Long tableId; // null ถ้าเป็น Takeaway
    @NotNull

    @Size(min = 1, message = "At least one item is required")
    private List<OrderItemRequestDTO> items;


}
