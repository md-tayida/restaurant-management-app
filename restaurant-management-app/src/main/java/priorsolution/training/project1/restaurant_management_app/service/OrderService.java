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
import priorsolution.training.project1.restaurant_management_app.entity.enums.TableStatusEnum;
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
    private final MenuRepository menuRepository;
    private final OrderMapper orderMapper;
    private final TableInfoService tableInfoService;

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO dto) {
        if (dto == null || dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BadRequestException("Order items cannot be empty", "ORDER_ITEMS_EMPTY");
        }

        OrderEntity order;

        // กรณี DINE_IN
        if (dto.getOrderType() == OrderTypeEnum.DINE_IN) {
            if (dto.getTableId() == null) {
                throw new BadRequestException("Table ID is required for dine-in orders", "TABLE_ID_REQUIRED");
            }

            // เช็กว่าโต๊ะมีออเดอร์ ACTIVE หรือไม่
            Optional<OrderEntity> existingOrderOpt = orderRepository
                    .findByTableIdAndStatus(dto.getTableId(), OrderStatusEnum.ACTIVE);

            if (existingOrderOpt.isPresent()) {
                // เพิ่มเมนูเข้าออเดอร์เดิม
                order = existingOrderOpt.get();

                List<OrderItemEntity> newItems = dto.getItems().stream().map(itemDTO -> {
                    MenuEntity menu = menuRepository.findById(itemDTO.getMenuId())
                            .orElseThrow(() -> new ResourceNotFoundException("Menu with ID " + itemDTO.getMenuId() + " not found", "MENU_NOT_FOUND"));
                    return OrderItemMapper.toEntity(itemDTO, menu, order);
                }).collect(Collectors.toList());

                order.getOrderItems().addAll(newItems);
                order.setTotalPrice(PriceCalculatorUtil.calculateTotalPrice(order.getOrderItems()));
            } else {
                // ไม่มีออเดอร์ ACTIVE → สร้างใหม่
                order = orderMapper.toEntity(dto);

                tableInfoService.setTableStatus(dto.getTableId(), TableStatusEnum.OCCUPIED);

                for (OrderItemEntity item : order.getOrderItems()) {
                    menuRepository.findById(item.getMenu().getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Menu with ID " + item.getMenu().getId() + " not found", "MENU_NOT_FOUND"));
                }

                order.setTotalPrice(PriceCalculatorUtil.calculateTotalPrice(order.getOrderItems()));

                // เปลี่ยนสถานะโต๊ะเป็น OCCUPIED
                tableInfoService.setTableStatus(dto.getTableId(), TableStatusEnum.OCCUPIED);
            }

        } else {
            // กรณี TAKEAWAY
            order = orderMapper.toEntity(dto);

            for (OrderItemEntity item : order.getOrderItems()) {
                menuRepository.findById(item.getMenu().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Menu with ID " + item.getMenu().getId() + " not found", "MENU_NOT_FOUND"));
            }

            order.setTotalPrice(PriceCalculatorUtil.calculateTotalPrice(order.getOrderItems()));
        }

        OrderEntity saved = orderRepository.save(order);
        return orderMapper.toDto(saved);
    }

    public List<OrderResponseDTO> getAllOrders() {
        List<OrderEntity> orders = orderRepository.findAll();

        return orders.stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    /// /*************
    @Transactional
    public OrderResponseDTO payOrder(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + orderId + " not found", "ORDER_NOT_FOUND"));

        if (order.getStatus() != OrderStatusEnum.ACTIVE) {
            throw new BadRequestException("Order must be in ACTIVE status to be paid", "INVALID_ORDER_STATUS");
        }

        // ตรวจสอบว่าเมนูทั้งหมด DONE แล้วหรือยัง
        List<OrderItemEntity> unfinishedItems = order.getOrderItems().stream()
                .filter(item -> item.getStatus() != OrderItemStatusEnum.DONE)
                .collect(Collectors.toList());

        if (!unfinishedItems.isEmpty()) {
            String itemNames = unfinishedItems.stream()
                    .map(item -> item.getMenu().getName()) // สมมุติว่า MenuEntity มี .getName()
                    .collect(Collectors.joining(", "));

            throw new BadRequestException(
                    "Cannot complete payment. The following items are not yet done: " + itemNames,
                    "ORDER_ITEMS_NOT_DONE"
            );
        }

        order.setStatus(OrderStatusEnum.PAID);
        OrderEntity savedOrder = orderRepository.save(order);

        if (order.getOrderType() == OrderTypeEnum.DINE_IN && order.getTable() != null) {
            tableInfoService.setTableStatus(order.getTable().getId(), TableStatusEnum.AVAILABLE);
        }

        return orderMapper.toDto(savedOrder);
    }

    ///
    ///

    @Transactional
    public OrderResponseDTO cancelOrder(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + orderId + " not found", "ORDER_NOT_FOUND"));

        if (order.getStatus() == OrderStatusEnum.PAID || order.getStatus() == OrderStatusEnum.CANCELED) {
            throw new BadRequestException("Order is already completed or canceled", "CANNOT_CANCEL");
        }

        order.setStatus(OrderStatusEnum.CANCELED);
        OrderEntity savedOrder = orderRepository.save(order);

        // เปลี่ยนสถานะโต๊ะเป็น AVAILABLE หากเป็น DINE_IN
        if (order.getOrderType() == OrderTypeEnum.DINE_IN && order.getTable() != null) {
            tableInfoService.setTableStatus(order.getTable().getId(), TableStatusEnum.AVAILABLE);
        }

        return orderMapper.toDto(savedOrder);
    }

    @Transactional
    public void recalculateTotalPrice(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found", "ORDER_NOT_FOUND"));

        BigDecimal newTotal = PriceCalculatorUtil.calculateTotalPrice(
                order.getOrderItems().stream()
                        .filter(item -> item.getStatus() != OrderItemStatusEnum.CANCELED) // ไม่รวมรายการที่ถูกยกเลิก
                        .collect(Collectors.toList())
        );

        order.setTotalPrice(newTotal);
        orderRepository.save(order);
    }

}
