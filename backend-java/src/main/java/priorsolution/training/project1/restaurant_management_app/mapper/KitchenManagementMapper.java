package priorsolution.training.project1.restaurant_management_app.mapper;

import priorsolution.training.project1.restaurant_management_app.Response.KitchenManagementResponse;
import priorsolution.training.project1.restaurant_management_app.entity.OrderEntity;

import java.util.stream.Collectors;

public class KitchenManagementMapper {
    public static KitchenManagementResponse toInfoResponse(OrderEntity entity) {
        KitchenManagementResponse response = new KitchenManagementResponse();
        response.setId(entity.getId());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setOrderType(entity.getOrderType() != null ? entity.getOrderType() : null);
        response.setTableId(entity.getTable() != null ? entity.getTable().getId() : null);
        response.setTotalPrice(entity.getTotalPrice());

        if (entity.getOrderItems() != null) {
            response.setOrderItems(
                    entity.getOrderItems().stream()
                            .map(OrderItemMapper::toOrderItemResponse)
                            .collect(Collectors.toList())
            );
        }


        return response;
    }
}
