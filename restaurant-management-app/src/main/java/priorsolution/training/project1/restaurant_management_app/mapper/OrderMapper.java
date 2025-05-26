package priorsolution.training.project1.restaurant_management_app.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import priorsolution.training.project1.restaurant_management_app.dto.*;
import priorsolution.training.project1.restaurant_management_app.entity.*;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderItemStatusEnum;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderStatusEnum;
import priorsolution.training.project1.restaurant_management_app.entity.enums.TableStatusEnum;
import priorsolution.training.project1.restaurant_management_app.exception.ResourceNotFoundException;
import priorsolution.training.project1.restaurant_management_app.repository.MenuRepository;
import priorsolution.training.project1.restaurant_management_app.repository.TableInfoRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final MenuRepository menuRepository;
    private final TableInfoRepository tableInfoRepository;

    public OrderEntity toEntity(OrderRequestDTO dto) {
        OrderEntity order = new OrderEntity();
      //  order.setCreatedAt(LocalDateTime.now());
        order.setOrderType(dto.getOrderType());
        order.setStatus(OrderStatusEnum.ACTIVE);


        if (dto.getTableId() != null) {
            TableInfoEntity table = tableInfoRepository.findById(dto.getTableId())
                    .orElseThrow(() -> new ResourceNotFoundException("Table not found","NOT FOUND"));
            table.setStatus(TableStatusEnum.OCCUPIED);
            tableInfoRepository.save(table);
            order.setTable(table);
        }

        List<OrderItemEntity> items = dto.getItems().stream().map(itemDTO -> {
            MenuEntity menu = menuRepository.findById(itemDTO.getMenuId())
           .orElseThrow(() -> new ResourceNotFoundException("Menu not found","NOT FOUND"));
//            BigDecimal itemPrice = menu.getPrice(); // ราคาต่อหน่วย
//            int quantity = itemDTO.getQuantity();
//            BigDecimal totalPrice = itemPrice.multiply(BigDecimal.valueOf(quantity));

            return OrderItemMapper.toEntity(itemDTO, menu, order);
        }).collect(Collectors.toList());

        order.setOrderItems(items);
        return order;
    }

    public OrderResponseDTO toDto(OrderEntity order) {
        return OrderResponseDTO.builder()
                .id(order.getId())
                .orderType(order.getOrderType())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .totalPrice(order.getTotalPrice())
                .tableId(order.getTable() != null ? order.getTable().getId() : null)
                .items(order.getOrderItems().stream().map(item -> OrderItemResponseDTO.builder()
                        .id(item.getId())
                        .menuId(item.getMenu().getId())
                        .menuName(item.getMenu().getName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .status(item.getStatus())
                        .build()
                ).collect(Collectors.toList()))
                .build();
    }

}
