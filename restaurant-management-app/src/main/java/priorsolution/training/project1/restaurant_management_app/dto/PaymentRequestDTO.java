package priorsolution.training.project1.restaurant_management_app.dto;

import lombok.Builder;
import lombok.Data;
import priorsolution.training.project1.restaurant_management_app.entity.enums.PaymentMethodEnum;
@Data
@Builder
public class
PaymentRequestDTO {
    private Long tableId;
    private PaymentMethodEnum method;
}
