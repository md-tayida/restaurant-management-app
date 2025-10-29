package priorsolution.training.project1.restaurant_management_app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import priorsolution.training.project1.restaurant_management_app.Response.PaymentResponse;
import priorsolution.training.project1.restaurant_management_app.Response.PaymentsByStatusResponse;
import priorsolution.training.project1.restaurant_management_app.entity.OrderEntity;
import priorsolution.training.project1.restaurant_management_app.entity.PaymentEntity;
import priorsolution.training.project1.restaurant_management_app.entity.TableInfoEntity;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderItemStatusEnum;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderStatusEnum;
import priorsolution.training.project1.restaurant_management_app.entity.enums.PaymentStatusEnum;
import priorsolution.training.project1.restaurant_management_app.entity.enums.TableStatusEnum;
import priorsolution.training.project1.restaurant_management_app.exception.BadRequestException;
import priorsolution.training.project1.restaurant_management_app.exception.ResourceNotFoundException;
import priorsolution.training.project1.restaurant_management_app.mapper.PaymentMapper;
import priorsolution.training.project1.restaurant_management_app.repository.OrderRepository;
import priorsolution.training.project1.restaurant_management_app.repository.PaymentRepository;
import priorsolution.training.project1.restaurant_management_app.repository.TableInfoRepository;
import priorsolution.training.project1.restaurant_management_app.request.PaymentRequest;
import priorsolution.training.project1.restaurant_management_app.request.PaymentStatusRequest;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class PaymentService {

    private final OrderRepository orderRepository;
    private final TableInfoRepository tableInfoRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;


    @Transactional
    public PaymentResponse createPaymentByOrderId(PaymentRequest request) {
        OrderEntity orderEntity = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found", "ORDER_NOT_FOUND"));

        if (orderEntity.getPayment() != null) {
            throw new IllegalStateException("Payment already exists for this order");
        }

        if (orderEntity.getStatus() != OrderStatusEnum.ACTIVE) {
            throw new IllegalStateException("Cannot create payment for non-active order");
        }

        boolean hasUnfinishedItem = orderEntity.getOrderItems().stream()
                .anyMatch(item ->
                        item.getStatus() != OrderItemStatusEnum.SERVED &&
                                item.getStatus() != OrderItemStatusEnum.CANCELED
                );

        if (hasUnfinishedItem) {
            throw new IllegalStateException("Cannot create payment: Some order items are not served or canceled yet.");
        }

        PaymentEntity paymentEntity = PaymentMapper.toPaymentEntity(request, orderEntity);
        PaymentEntity saved = paymentRepository.save(paymentEntity);
        return PaymentMapper.toPaymentResponse(saved);

    }
    @Transactional
    public PaymentResponse updatePayment(Long paymentId, PaymentRequest request) {
        PaymentEntity paymentEntity = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found", "PAYMENT_NOT_FOUND"));

        PaymentStatusEnum currentStatus = paymentEntity.getStatus();
        PaymentStatusEnum requestStatus = request.getStatus();

        if (requestStatus == null) {
            throw new BadRequestException("Payment status is required", "PAYMENT_STATUS_REQUIRED");
        }

        switch (currentStatus) {
            case PENDING -> {
                if (requestStatus != PaymentStatusEnum.PAID && requestStatus != PaymentStatusEnum.FAILED) {
                    throw new BadRequestException(
                            "Invalid status transition: PENDING can only change to PAID or FAILED",
                            "INVALID_STATUS_TRANSITION"
                    );
                }
            }
            case PAID -> {
                if (requestStatus != PaymentStatusEnum.REFUNDED) {
                    throw new BadRequestException(
                            "Invalid status transition: PAID can only change to REFUND",
                            "INVALID_STATUS_TRANSITION"
                    );
                }
            }
            default -> throw new BadRequestException(
                    "Invalid current payment status for update: " + currentStatus,
                    "INVALID_CURRENT_STATUS"
            );
        }

        PaymentMapper.updatePaymentEntity(paymentEntity, request);

        PaymentEntity updated = paymentRepository.save(paymentEntity);
        TableInfoEntity table = tableInfoRepository.findById(paymentEntity.getOrder().getTable().getId())
                .orElseThrow(() -> new RuntimeException("Table not found"));

        table.setStatus(TableStatusEnum.AVAILABLE);
        tableInfoRepository.save(table);

        return PaymentMapper.toPaymentResponse(updated);
    }

    public List<PaymentsByStatusResponse> getPaymentsByStatus(PaymentStatusEnum request) {
        List<PaymentEntity> entities = paymentRepository.findByStatus(request);
        return entities.stream()
                .map(PaymentMapper::toPaymentByStatusResponse)
                .toList();
    }

}
