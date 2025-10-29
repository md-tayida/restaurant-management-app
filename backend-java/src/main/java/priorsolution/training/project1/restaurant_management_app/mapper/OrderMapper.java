package priorsolution.training.project1.restaurant_management_app.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import priorsolution.training.project1.restaurant_management_app.Response.OrderItemResponse;
import priorsolution.training.project1.restaurant_management_app.Response.OrderResponse;
import priorsolution.training.project1.restaurant_management_app.Response.PaymentResponse;
import priorsolution.training.project1.restaurant_management_app.dto.*;
import priorsolution.training.project1.restaurant_management_app.entity.*;

import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderItemStatusEnum;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderStatusEnum;

import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderTypeEnum;
import priorsolution.training.project1.restaurant_management_app.exception.ResourceNotFoundException;
import priorsolution.training.project1.restaurant_management_app.repository.MenuRepository;

import priorsolution.training.project1.restaurant_management_app.request.OrderItemRequest;
import priorsolution.training.project1.restaurant_management_app.request.OrderRequest;
import priorsolution.training.project1.restaurant_management_app.service.TableInfoService;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
@Component

public class OrderMapper {
    private final MenuRepository menuRepository;
    private final TableInfoService tableInfoService;

    public OrderMapper(MenuRepository menuRepository, TableInfoService tableInfoService) {
        this.menuRepository = menuRepository;
        this.tableInfoService = tableInfoService;
    }

  public static OrderResponse toInfoResponse(OrderEntity entity) {
            OrderResponse response = new OrderResponse();
            response.setId(entity.getId());
            response.setCreatedAt(entity.getCreatedAt());
            response.setUpdatedAt(entity.getUpdatedAt());
            response.setDeletedAt(entity.getDeletedAt());
            response.setOrderType(entity.getOrderType() != null ? entity.getOrderType() : null);
            response.setStatus(entity.getStatus() != null ? entity.getStatus() : null);
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




//    private static PaymentResponse toPaymentResponse(PaymentEntity payment) {
//        if (payment == null) return null;
//
//        PaymentResponse response = new PaymentResponse();
//        response.setId(payment.getId());
//        response.setAmount(payment.getAmount());
//        response.setMethod(payment.getMethod());
//        response.setCreatedAt(payment.getCreatedAt());
//        response.setUpdatedAt(payment.getUpdatedAt());
//
//        return response;
//    }

    public static OrderEntity mapToOrderCreateEntity(OrderRequest request, TableInfoEntity table, MenuRepository menuRepository) {
        OrderEntity order = new OrderEntity();

        order.setOrderType(request.getOrderType());
        order.setStatus(OrderStatusEnum.ACTIVE);

        order.setTable(table);

        List<OrderItemEntity> orderItems = request.getOrderItems().stream()
                .map(itemReq -> mapToOrderItemCreateEntity(itemReq, order, menuRepository))
                .collect(Collectors.toList());

        order.setOrderItems(orderItems);

        BigDecimal totalPrice = orderItems.stream()
                .map(item -> item.getMenu().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalPrice(totalPrice);

        return order;
    }

    private static OrderItemEntity mapToOrderItemCreateEntity(OrderItemRequest request, OrderEntity order, MenuRepository menuRepository) {
        MenuEntity menu = menuRepository.findById(request.getMenuId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found", "MENU_NOT_FOUND"));

        OrderItemEntity item = new OrderItemEntity();
        item.setMenu(menu);
        item.setQuantity(request.getQuantity());
        item.setDescription(request.getDescription());
        item.setStatus(OrderItemStatusEnum.PENDING);
        item.setOrder(order);
        item.setPrice(menu.getPrice());
        return item;
    }



}
