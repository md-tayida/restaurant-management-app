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
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderTypeEnum;
import priorsolution.training.project1.restaurant_management_app.exception.BadRequestException;
import priorsolution.training.project1.restaurant_management_app.exception.ResourceNotFoundException;
import priorsolution.training.project1.restaurant_management_app.mapper.MenuMapper;
import priorsolution.training.project1.restaurant_management_app.mapper.OrderItemMapper;
import priorsolution.training.project1.restaurant_management_app.mapper.OrderMapper;
import priorsolution.training.project1.restaurant_management_app.repository.*;
import priorsolution.training.project1.restaurant_management_app.util.PriceCalculatorUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

        OrderEntity order;

        // เช็กเฉพาะกรณี DINE_IN เท่านั้น
        if (dto.getOrderType() == OrderTypeEnum.DINE_IN) {
            if (dto.getTableId() == null) {
                throw new BadRequestException("Table ID is required for dine-in orders", "TABLE_ID_REQUIRED");
            }

            // ตรวจสอบว่าโต๊ะนี้มีออเดอร์ ACTIVE อยู่หรือไม่
            Optional<OrderEntity> existingOrderOpt = orderRepository
                    .findByTableIdAndStatus(dto.getTableId(), OrderStatusEnum.ACTIVE);

            if (existingOrderOpt.isPresent()) {
                // โต๊ะมีออเดอร์ ACTIVE → update
                order = existingOrderOpt.get();

                // เพิ่ม items ใหม่เข้าไป
                List<OrderItemEntity> newItems = dto.getItems().stream().map(itemDTO -> {
                    MenuEntity menu = menuRepository.findById(itemDTO.getMenuId())
                            .orElseThrow(() -> new ResourceNotFoundException("Menu with ID " + itemDTO.getMenuId() + " not found", "MENU_NOT_FOUND"));
                    return OrderItemMapper.toEntity(itemDTO, menu, order);
                }).collect(Collectors.toList());

                order.getOrderItems().addAll(newItems);

                // คำนวณราคาทั้งหมดใหม่
                BigDecimal total = PriceCalculatorUtil.calculateTotalPrice(order.getOrderItems());
                order.setTotalPrice(total);
            } else {
                // โต๊ะไม่มีออเดอร์ → สร้างใหม่
                order = orderMapper.toEntity(dto);

                for (OrderItemEntity item : order.getOrderItems()) {
                    // validate ว่าเมนูมีจริง
                    menuRepository.findById(item.getMenu().getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Menu with ID " + item.getMenu().getId() + " not found", "MENU_NOT_FOUND"));
                }

                BigDecimal total = PriceCalculatorUtil.calculateTotalPrice(order.getOrderItems());
                order.setTotalPrice(total);
            }

        } else {
            // takeaway → สร้างใหม่
            order = orderMapper.toEntity(dto);

            for (OrderItemEntity item : order.getOrderItems()) {
                menuRepository.findById(item.getMenu().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Menu with ID " + item.getMenu().getId() + " not found", "MENU_NOT_FOUND"));
            }

            BigDecimal total = PriceCalculatorUtil.calculateTotalPrice(order.getOrderItems());
            order.setTotalPrice(total);
        }

        OrderEntity saved = orderRepository.save(order);
        return orderMapper.toDto(saved);
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
