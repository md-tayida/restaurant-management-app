package priorsolution.training.project1.restaurant_management_app.dto;

import lombok.Getter;
import lombok.Setter;
import priorsolution.training.project1.restaurant_management_app.entity.enums.TableStatusEnum;
@Setter
@Getter
public class TableInfoStatusRequestDTO {
    private  TableStatusEnum status;


}
