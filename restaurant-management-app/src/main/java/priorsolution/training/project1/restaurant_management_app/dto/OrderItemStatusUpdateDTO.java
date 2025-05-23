package priorsolution.training.project1.restaurant_management_app.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class OrderItemStatusUpdateDTO  {
    private Long id;
    private String tableNumber;
    private String menuName;
    private int quantity;
    private String status;

}
