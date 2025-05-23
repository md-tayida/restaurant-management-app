package priorsolution.training.project1.restaurant_management_app.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.query.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import priorsolution.training.project1.restaurant_management_app.dto.*;
import priorsolution.training.project1.restaurant_management_app.entity.*;

//import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderItemStatusEnum;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderItemStatusEnum;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderStatusEnum;
import priorsolution.training.project1.restaurant_management_app.exception.BadRequestException;
import priorsolution.training.project1.restaurant_management_app.exception.ResourceNotFoundException;
import priorsolution.training.project1.restaurant_management_app.mapper.MenuMapper;
import priorsolution.training.project1.restaurant_management_app.mapper.OrderMapper;
import priorsolution.training.project1.restaurant_management_app.repository.*;
import priorsolution.training.project1.restaurant_management_app.util.PriceCalculatorUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final TableInfoRepository tableInfoRepository;
    private final MenuRepository menuRepository;
    private final OrderMapper orderMapper;
    //   private final OrderItemRepository orderItemRepository;

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO dto) {
        if (dto == null || dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BadRequestException("Order items cannot be empty", "ORDER_ITEMS_EMPTY");
        }

        OrderEntity order = orderMapper.toEntity(dto); // แปลง DTO ไปเป็น Entity

        BigDecimal total = PriceCalculatorUtil.calculateTotalPrice(order.getOrderItems());
        order.setTotalPrice(total);

        OrderEntity savedOrder = orderRepository.save(order); // บันทึกลง DB
        return orderMapper.toDto(savedOrder); // แปลงกลับเป็น OrderResponseDTO เพื่อคืนให้ client
    }

    public List<OrderResponseDTO> getAllOrders() {
        List<OrderEntity> orders = orderRepository.findAll();
        if (orders.isEmpty()) {
            throw new ResourceNotFoundException("No orders found", "ORDERS_NOT_FOUND");
        }
        return orders.stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }
}
