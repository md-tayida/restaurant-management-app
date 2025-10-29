package priorsolution.training.project1.restaurant_management_app.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import priorsolution.training.project1.restaurant_management_app.Response.OrderItemResponse;
import priorsolution.training.project1.restaurant_management_app.entity.OrderItemEntity;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderItemStatusEnum;
import priorsolution.training.project1.restaurant_management_app.exception.ResourceNotFoundException;
import priorsolution.training.project1.restaurant_management_app.mapper.OrderItemMapper;
import priorsolution.training.project1.restaurant_management_app.repository.OrderItemRepository;
import priorsolution.training.project1.restaurant_management_app.request.OrderItemStatusRequest;

import java.util.List;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
private final OrderService orderService;
    public OrderItemService(OrderItemRepository orderItemRepository, OrderService orderService) {
        this.orderItemRepository = orderItemRepository;
        this.orderService = orderService;
    }
    @Transactional
    public OrderItemResponse updateOrderItemStatus(Long orderItemId, OrderItemStatusRequest request) {
        OrderItemEntity item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found", "ORDER_ITEM_NOT_FOUND"));

        OrderItemStatusEnum currentStatus = item.getStatus();
        OrderItemStatusEnum newStatus = request.getOrderItemStatus();

        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new IllegalStateException(
                    String.format("Invalid status transition: %s → %s", currentStatus, newStatus)
            );
        }

        item.setStatus(newStatus);
        OrderItemEntity saved = orderItemRepository.save(item);

        return OrderItemMapper.toOrderItemResponse(saved);
    }

    private boolean isValidStatusTransition(OrderItemStatusEnum current, OrderItemStatusEnum next) {
        return switch (current) {
            case PENDING -> next == OrderItemStatusEnum.IN_PROGRESS || next == OrderItemStatusEnum.CANCELED;
            case IN_PROGRESS -> next == OrderItemStatusEnum.READY || next == OrderItemStatusEnum.CANCELED;
            case READY -> next == OrderItemStatusEnum.SERVED || next == OrderItemStatusEnum.CANCELED;
            default -> false;
        };
    }

    @Transactional(readOnly = true)
    public List<OrderItemResponse> getAllOrderItems() {
        List<OrderItemEntity> entities = orderItemRepository.findAll();
        if (entities.isEmpty()) {
            throw new ResourceNotFoundException("No order item found", "ORDER_ITEMS_NOT_FOUND");
        }
        return entities.stream()
                .map(OrderItemMapper::toOrderItemResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderItemResponse getOrderItemById(Long id) {
        OrderItemEntity entity = orderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found", "TABLE_NOT_FOUND"));
        return OrderItemMapper.toOrderItemResponse(entity);
    }
}
