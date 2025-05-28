package priorsolution.training.project1.restaurant_management_app.dto;


import lombok.Data;
import priorsolution.training.project1.restaurant_management_app.entity.enums.MenuStatusEnum;

@Data
public class MenuStatusRequestDTO {
    private MenuStatusEnum status;
}
