package priorsolution.training.project1.restaurant_management_app.mapper;

import priorsolution.training.project1.restaurant_management_app.dto.OrderItemDTO;
import priorsolution.training.project1.restaurant_management_app.dto.OrderItemStatusUpdateDTO;
import priorsolution.training.project1.restaurant_management_app.entity.OrderItemEntity;

public class OrderItemMapper {

    public static OrderItemDTO toOrderItemDTO(OrderItemEntity item) {
        return OrderItemDTO.builder()
                .id(item.getId())
                .menuName(item.getMenu().getName())
                .quantity(item.getQuantity())
                .unitPrice(item.getPrice())
                .status(item.getStatus().name())
                .build();
    }
    public static OrderItemStatusUpdateDTO toDTO(OrderItemEntity item) {
        return OrderItemStatusUpdateDTO .builder()
                .id(item.getId())
                .menuName(item.getMenu().getName())
                .quantity(item.getQuantity())
                .status(item.getStatus().name())
                .tableNumber(item.getOrder().getTable() != null
                        ? item.getOrder().getTable().getTableNumber()
                        : "Takeaway")
                .build();
    }

}
