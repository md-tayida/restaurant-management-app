package priorsolution.training.project1.restaurant_management_app.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import priorsolution.training.project1.restaurant_management_app.entity.enums.TableStatusEnum;

import java.math.BigDecimal;
import java.util.List;

// TableWithOrdersDTO.java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderSummaryDTO {
    private Long tableId;
    private String tableNumber;
    private TableStatusEnum tableStatus;

    private BigDecimal totalPrice;
    private BigDecimal unitPrice;
    private List<OrderDTO> orders;
    //private List<OrderItemDTO> items;


    // Getters and Setters
}