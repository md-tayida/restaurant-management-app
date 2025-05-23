package priorsolution.training.project1.restaurant_management_app.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

// OrderItemDTO.java
@Data
@Builder
public class OrderItemDTO {
    private Long id;
    private String menuName;
    private int quantity;
    private BigDecimal unitPrice;
    private String status;
}
