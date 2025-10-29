package priorsolution.training.project1.restaurant_management_app.request;


import lombok.Getter;
import lombok.Setter;
import priorsolution.training.project1.restaurant_management_app.entity.enums.TableStatusEnum;

@Getter
@Setter
public class TableInfoRequest {
    private Long id;
    private Long tableNumber;
    private TableStatusEnum status;

}
