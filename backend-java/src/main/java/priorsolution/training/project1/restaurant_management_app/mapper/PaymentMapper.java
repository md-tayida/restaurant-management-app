package priorsolution.training.project1.restaurant_management_app.mapper;

import org.springframework.stereotype.Component;
import priorsolution.training.project1.restaurant_management_app.Response.PaymentResponse;
import priorsolution.training.project1.restaurant_management_app.Response.PaymentsByStatusResponse;
import priorsolution.training.project1.restaurant_management_app.entity.OrderEntity;
import priorsolution.training.project1.restaurant_management_app.entity.PaymentEntity;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderStatusEnum;
import priorsolution.training.project1.restaurant_management_app.entity.enums.PaymentStatusEnum;
import priorsolution.training.project1.restaurant_management_app.request.PaymentRequest;

import java.time.LocalDateTime;

@Component
public class PaymentMapper {

    public static PaymentEntity toPaymentEntity(PaymentRequest request, OrderEntity orderEntity) {
        PaymentEntity entity = new PaymentEntity();
        entity.setOrder(orderEntity);
        entity.setAmount(
                request.getAmount() != null ? request.getAmount() : orderEntity.getTotalPrice()
        );
        entity.setMethod(request.getPaymentMethod());
        entity.setStatus(
                request.getStatus() != null ? request.getStatus() : PaymentStatusEnum.PENDING
        );
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }

    public static PaymentResponse toPaymentResponse(PaymentEntity paymentEntity) {
        if (paymentEntity == null) return null;

        PaymentResponse response = new PaymentResponse();
        response.setId(paymentEntity.getId());
        response.setOrderId(paymentEntity.getOrder().getId());
        response.setAmount(paymentEntity.getAmount());
        response.setMethod(paymentEntity.getMethod());
        response.setStatus(paymentEntity.getStatus());
        response.setCreatedAt(paymentEntity.getCreatedAt());
        response.setUpdatedAt(paymentEntity.getUpdatedAt());
        return response;
    }

    public static void updatePaymentEntity(PaymentEntity entity, PaymentRequest request) {
        if (request.getPaymentMethod() != null) {
            entity.setMethod(request.getPaymentMethod());
        }
        if (request.getAmount() != null) {
            entity.setAmount(request.getAmount());
        }
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }

        entity.setUpdatedAt(LocalDateTime.now());

        if (request.getStatus() == PaymentStatusEnum.PAID){
        entity.getOrder().setStatus(OrderStatusEnum.COMPLETED);

        }

        if (request.getStatus() == PaymentStatusEnum.REFUNDED){
            entity.getOrder().setStatus(OrderStatusEnum.CANCELED);

        }
        entity.getOrder().setCreatedAt(LocalDateTime.now());
    }

    public static PaymentsByStatusResponse toPaymentByStatusResponse(PaymentEntity paymentEntity) {
        PaymentsByStatusResponse response = new PaymentsByStatusResponse();
        response.setId(paymentEntity.getId());
        response.setAmount(paymentEntity.getAmount());
        response.setMethod(paymentEntity.getMethod());
        response.setStatus(paymentEntity.getStatus());
        response.setCreatedAt(paymentEntity.getCreatedAt());
        response.setUpdatedAt(paymentEntity.getUpdatedAt());
        response.setOrderResponse(OrderMapper.toInfoResponse(paymentEntity.getOrder()));
        return response;
    }

}
