package priorsolution.training.project1.restaurant_management_app.mapper;

//import priorsolution.training.project1.restaurant_management_app.dto.OrderItemDTO;
import priorsolution.training.project1.restaurant_management_app.dto.OrderItemRequestDTO;
import priorsolution.training.project1.restaurant_management_app.dto.OrderItemStatusUpdateDTO;
import priorsolution.training.project1.restaurant_management_app.entity.MenuEntity;
import priorsolution.training.project1.restaurant_management_app.entity.OrderEntity;
import priorsolution.training.project1.restaurant_management_app.entity.OrderItemEntity;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderItemStatusEnum;

import java.math.BigDecimal;

public class OrderItemMapper {
    private OrderItemRequestDTO orderItemRequestDTO;
    private MenuEntity menu;



    public static OrderItemEntity toEntity(OrderItemRequestDTO dto, MenuEntity menu, OrderEntity order) {
        BigDecimal totalPrice = menu.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity()));
        return OrderItemEntity.builder()
                .menu(menu)
                .quantity(dto.getQuantity())
//                .price(menu.getPrice())
                .price(totalPrice)
                .status(OrderItemStatusEnum.PREPARING)
                .order(order)
                .build();
    }

    public static OrderItemStatusUpdateDTO toDTO(OrderItemEntity item) {
        return OrderItemStatusUpdateDTO .builder()
                .id(item.getId())
                .menuName(item.getMenu().getName())
                .createTime(item.getCreatedAt())
                .quantity(item.getQuantity())
                .status(item.getStatus().name())
                .tableNumber(item.getOrder().getTable() != null
                        ? item.getOrder().getTable().getTableNumber()
                        : "Takeaway")
                .build();
    }

}
