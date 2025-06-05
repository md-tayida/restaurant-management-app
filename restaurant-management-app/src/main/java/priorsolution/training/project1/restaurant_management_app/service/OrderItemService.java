package priorsolution.training.project1.restaurant_management_app.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import priorsolution.training.project1.restaurant_management_app.dto.OrderItemStatusUpdateDTO;
import priorsolution.training.project1.restaurant_management_app.entity.OrderItemEntity;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderItemStatusEnum;
import priorsolution.training.project1.restaurant_management_app.exception.ResourceNotFoundException;
import priorsolution.training.project1.restaurant_management_app.mapper.OrderItemMapper;
import priorsolution.training.project1.restaurant_management_app.repository.OrderItemRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
private final OrderService orderService;
    public OrderItemService(OrderItemRepository orderItemRepository, OrderService orderService) {
        this.orderItemRepository = orderItemRepository;
        this.orderService = orderService;
    }
    @Transactional(readOnly = true)
    public List<OrderItemStatusUpdateDTO> getAllPreparingItems() {
        List<OrderItemEntity> items = orderItemRepository.findAllByStatus(OrderItemStatusEnum.PREPARING);

        if (items.isEmpty()) {
            throw new ResourceNotFoundException("No preparing items found", "PREPARING_ITEMS_NOT_FOUND");
        }

        return items.stream()
                .map(OrderItemMapper::toDTO)
                .collect(Collectors.toList());
    }




    @Transactional(readOnly = true)
    public List<OrderItemStatusUpdateDTO> getAllPreparingItems(String categoryName) {
        List<OrderItemEntity> items = orderItemRepository.findByMenu_Category_NameAndStatus(
                categoryName, OrderItemStatusEnum.PREPARING
        );

        if (items.isEmpty()) {
            throw new ResourceNotFoundException("No preparing items found for category: " + categoryName, "PREPARING_ITEMS_NOT_FOUND");
        }

        return items.stream()
                .map(OrderItemMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markItemReadyToServe(Long itemId) {
        OrderItemEntity item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + itemId, "ITEM_NOT_FOUND"));

        item.setStatus(OrderItemStatusEnum.READY_TO_SERVE);
        orderItemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public List<OrderItemStatusUpdateDTO> getReadyToServeItems() {
        List<OrderItemEntity> items = orderItemRepository.findAllByStatus(OrderItemStatusEnum.READY_TO_SERVE);

        if (items.isEmpty()) {
            throw new ResourceNotFoundException("No ready-to-serve items found", "READY_ITEMS_NOT_FOUND");
        }

        return items.stream()
                .map(OrderItemMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markItemDone(Long itemId) {
        OrderItemEntity item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + itemId, "ITEM_NOT_FOUND"));

        item.setStatus(OrderItemStatusEnum.DONE);
        orderItemRepository.save(item);
    }


    @Transactional
    public void cancelOrderItem(Long itemId) {

        OrderItemEntity item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found", "ITEM_NOT_FOUND"));

        // ✅ เช็คสถานะก่อนว่า cancel ได้ไหม
        if (item.getStatus() == OrderItemStatusEnum.CANCELED) {
            throw new IllegalStateException("Cannot cancel item that is already canceled");
        }

        item.setStatus(OrderItemStatusEnum.CANCELED);
        orderItemRepository.save(item);

        // ✅ เช็คว่า order ไม่ null ก่อนเรียก service
        if (item.getOrder() == null) {
            throw new IllegalStateException("Order reference is null for item id: " + itemId);
        }

        orderService.recalculateTotalPrice(item.getOrder().getId());
    }


}
