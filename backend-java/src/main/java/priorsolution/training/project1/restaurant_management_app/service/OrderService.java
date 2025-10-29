package priorsolution.training.project1.restaurant_management_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import priorsolution.training.project1.restaurant_management_app.Response.OrderResponse;
import priorsolution.training.project1.restaurant_management_app.entity.*;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderItemStatusEnum;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderStatusEnum;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderTypeEnum;
import priorsolution.training.project1.restaurant_management_app.entity.enums.TableStatusEnum;
import priorsolution.training.project1.restaurant_management_app.exception.ResourceNotFoundException;
import priorsolution.training.project1.restaurant_management_app.mapper.OrderMapper;
import priorsolution.training.project1.restaurant_management_app.repository.*;
import priorsolution.training.project1.restaurant_management_app.request.OrderRequest;
import priorsolution.training.project1.restaurant_management_app.request.OrderStatusRequest;

import java.time.LocalDateTime;
import java.util.List;


@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuRepository menuRepository;
    private final OrderItemRepository orderItemRepository;
    private final TableInfoRepository tableInfoRepository;

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        List<OrderEntity> entities = orderRepository.findAll();
        if (entities.isEmpty()) {
            throw new ResourceNotFoundException("No tables found", "TABLES_NOT_FOUND");
        }
        return entities.stream()
                .map(OrderMapper::toInfoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        OrderEntity entity = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found", "TABLE_NOT_FOUND"));
        return OrderMapper.toInfoResponse(entity);
    }


    @Transactional
    public OrderResponse createOrder(OrderRequest request) {

        TableInfoEntity table = null;
        if (request.getOrderType() == OrderTypeEnum.DINE_IN) {
            table = tableInfoRepository.findById(request.getTableId())
                    .orElseThrow(() -> new ResourceNotFoundException("Table not found", "TABLE_NOT_FOUND"));


            if (table.getStatus() != TableStatusEnum.AVAILABLE) {
                throw new IllegalStateException("Table is not available for new order");
            }

            table.setStatus(TableStatusEnum.OCCUPIED);
            tableInfoRepository.save(table);
        }

        OrderEntity entity = OrderMapper.mapToOrderCreateEntity(request, table, menuRepository);
        OrderEntity saved = orderRepository.save(entity);

        return OrderMapper.toInfoResponse(saved);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatusRequest request) {

        OrderEntity entity = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found", "ORDER_NOT_FOUND"));

        List<OrderItemEntity> orderItemEntities = orderItemRepository.findByOrderId(orderId);
        boolean hasUnservedItem = orderItemEntities.stream()
                .anyMatch(item ->
                        item.getStatus() != OrderItemStatusEnum.SERVED &&
                                item.getStatus() != OrderItemStatusEnum.CANCELED
                );

        if (request.getOrderStatus() == OrderStatusEnum.COMPLETED && hasUnservedItem) {
            throw new IllegalStateException("Cannot complete order: Some items are not served or canceled yet.");
        }
        if (entity.getOrderType() == OrderTypeEnum.DINE_IN &&
                entity.getTable() != null &&
                (request.getOrderStatus() == OrderStatusEnum.CANCELED ||
                        request.getOrderStatus() == OrderStatusEnum.COMPLETED)) {
            entity.getTable().setStatus(TableStatusEnum.AVAILABLE);
            tableInfoRepository.save(entity.getTable());
        }
        entity.setStatus(request.getOrderStatus());

        if (request.getOrderStatus() == OrderStatusEnum.CANCELED) {
            entity.setDeletedAt(LocalDateTime.now());
        }
        OrderEntity saved = orderRepository.save(entity);
        return OrderMapper.toInfoResponse(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponse getActiveOrderByTableId(Long tableId) {
        OrderEntity entity = orderRepository
                .findFirstByTableIdAndStatus(tableId, OrderStatusEnum.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Active order not found", "ORDER_NOT_FOUND"));


        return OrderMapper.toInfoResponse(entity);
    }


}
