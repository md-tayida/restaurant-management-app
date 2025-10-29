package priorsolution.training.project1.restaurant_management_app.request;

import lombok.Getter;
import lombok.Setter;
import priorsolution.training.project1.restaurant_management_app.entity.enums.PaymentMethodEnum;
import priorsolution.training.project1.restaurant_management_app.entity.enums.PaymentStatusEnum;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentRequest {
    private Long orderId;
    private PaymentMethodEnum paymentMethod;
    private PaymentStatusEnum status;
    private BigDecimal amount;
}
