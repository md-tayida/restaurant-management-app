package priorsolution.training.project1.restaurant_management_app.mapper;

import org.springframework.stereotype.Component;
import priorsolution.training.project1.restaurant_management_app.dto.PaymentRequestDTO;
import priorsolution.training.project1.restaurant_management_app.entity.OrderEntity;
import priorsolution.training.project1.restaurant_management_app.entity.PaymentEntity;

import java.time.LocalDateTime;

@Component
public class PaymentMapper {

    public PaymentEntity toPaymentEntity(OrderEntity order) {
        PaymentEntity payment = new PaymentEntity();
        payment.setAmount(order.getTotalPrice());
        payment.setPaidAt(LocalDateTime.now());
        payment.setOrder(order);
        return payment;
    }

    // ถ้ามี DTO อื่นๆที่ต้อง mapping สามารถเพิ่มที่นี่ได้
}
