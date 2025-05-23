package priorsolution.training.project1.restaurant_management_app.dto;


import lombok.Data;

import java.util.List;

@Data
public class OrderItemRequestDTO {

    private Long menuId;
    private int quantity;

}
