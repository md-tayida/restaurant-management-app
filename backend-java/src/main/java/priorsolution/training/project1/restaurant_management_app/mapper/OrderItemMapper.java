package priorsolution.training.project1.restaurant_management_app.mapper;

//import priorsolution.training.project1.restaurant_management_app.dto.OrderItemDTO;
import priorsolution.training.project1.restaurant_management_app.Response.OrderItemResponse;
import priorsolution.training.project1.restaurant_management_app.entity.MenuEntity;
import priorsolution.training.project1.restaurant_management_app.entity.OrderEntity;
import priorsolution.training.project1.restaurant_management_app.entity.OrderItemEntity;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderItemStatusEnum;
import priorsolution.training.project1.restaurant_management_app.exception.ResourceNotFoundException;
import priorsolution.training.project1.restaurant_management_app.repository.MenuRepository;
import priorsolution.training.project1.restaurant_management_app.request.OrderItemRequest;

import java.math.BigDecimal;

public class OrderItemMapper {
    private MenuEntity menu;
private MenuRepository menuRepository;



    public static OrderItemEntity mapToOrderItemCreateEntity(OrderItemRequest request, OrderEntity orderEntity, MenuEntity menu) {
        OrderItemEntity entity = new OrderItemEntity();
        entity.setMenu(menu);
        entity.setQuantity(request.getQuantity());
        entity.setDescription(request.getDescription());
        entity.setStatus(OrderItemStatusEnum.PENDING);
        entity.setOrder(orderEntity);

        return entity;
    }


    public static OrderItemResponse toOrderItemResponse(OrderItemEntity item) {
        if (item == null) return null;

        OrderItemResponse response = new OrderItemResponse();
        response.setId(item.getId());
        response.setMenuId(item.getMenu() != null ? item.getMenu().getId() : null);
        response.setMenuName(item.getMenu() != null ? item.getMenu().getName() : null);
        response.setQuantity(item.getQuantity());
        response.setPrice(item.getPrice());
        response.setDescription(item.getDescription());
        response.setStatus(item.getStatus());
        response.setCreatedAt(item.getCreatedAt());
        response.setUpdatedAt(item.getUpdatedAt());
        return response;
    }

}
