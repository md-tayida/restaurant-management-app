//package priorsolution.training.project1.restaurant_management_app.service;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.server.ResponseStatusException;
//import priorsolution.training.project1.restaurant_management_app.dto.PaymentRequestDTO;
//import priorsolution.training.project1.restaurant_management_app.entity.OrderEntity;
//import priorsolution.training.project1.restaurant_management_app.entity.TableInfoEntity;
//import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderItemStatusEnum;
//import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderStatusEnum;
//import priorsolution.training.project1.restaurant_management_app.entity.enums.TableStatusEnum;
//import priorsolution.training.project1.restaurant_management_app.mapper.PaymentMapper;
//import priorsolution.training.project1.restaurant_management_app.repository.OrderRepository;
//import priorsolution.training.project1.restaurant_management_app.repository.TableInfoRepository;
//
//import java.util.List;
//
//@Service
//public class PaymentService {
//
//    private final OrderRepository orderRepository;
//    private final TableInfoRepository tableInfoRepository;
//    private final PaymentMapper paymentMapper;
//
//    public PaymentService(OrderRepository orderRepository,
//                          TableInfoRepository tableInfoRepository,
//                          PaymentMapper paymentMapper) {
//        this.orderRepository = orderRepository;
//        this.tableInfoRepository = tableInfoRepository;
//        this.paymentMapper = paymentMapper;
//    }
//
//    @Transactional
//    public void payOrder(PaymentRequestDTO dto) {
//        TableInfoEntity table = tableInfoRepository.findById(dto.getTableId())
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ไม่พบโต๊ะ"));
//
//        List<OrderEntity> orders = orderRepository.findByTableIdAndStatus(dto.getTableId(), OrderStatusEnum.ACTIVE);
//
//        if (orders.isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ไม่มีออเดอร์ที่รอชำระเงินสำหรับโต๊ะนี้");
//        }
//
//        for (OrderEntity order : orders) {
//            boolean hasUndoneItem = order.getOrderItems().stream()
//                    .anyMatch(item -> item.getStatus() == OrderItemStatusEnum.PREPARING
//                            || item.getStatus() == OrderItemStatusEnum.READY_TO_SERVE);
//
//            if (hasUndoneItem) {
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//                        "ยังมีเมนูที่ยังไม่เสิร์ฟหรือยังเตรียมอยู่ใน order ID: " + order.getId());
//            }
//
//            order.setPayment(paymentMapper.toPaymentEntity(order));
//            order.setStatus(OrderStatusEnum.PAID);
//        }
//
//        table.setStatus(TableStatusEnum.AVAILABLE);
//
//        tableInfoRepository.save(table);
//        orderRepository.saveAll(orders);
//    }
//}
