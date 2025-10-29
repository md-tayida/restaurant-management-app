package priorsolution.training.project1.restaurant_management_app.Response;


import lombok.Getter;
import lombok.Setter;
import priorsolution.training.project1.restaurant_management_app.entity.enums.TableStatusEnum;

@Getter
@Setter
public class TableInfoResponse {
    private Long id;
    private Long tableNumber;
    private TableStatusEnum status;

}
