package priorsolution.training.project1.restaurant_management_app.Response;

import lombok.Getter;
import lombok.Setter;
import priorsolution.training.project1.restaurant_management_app.entity.enums.PaymentMethodEnum;
import priorsolution.training.project1.restaurant_management_app.entity.enums.PaymentStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
public class PaymentResponse {
    private Long id;

    private Long orderId;
    private BigDecimal amount;
    private PaymentMethodEnum method;
    private PaymentStatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;



}