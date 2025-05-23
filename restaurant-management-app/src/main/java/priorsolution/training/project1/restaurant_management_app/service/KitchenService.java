package priorsolution.training.project1.restaurant_management_app.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import priorsolution.training.project1.restaurant_management_app.dto.OrderItemStatusUpdateDTO;
import priorsolution.training.project1.restaurant_management_app.entity.OrderItemEntity;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderItemStatusEnum;
import priorsolution.training.project1.restaurant_management_app.mapper.OrderItemMapper;
import priorsolution.training.project1.restaurant_management_app.repository.OrderItemRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KitchenService {

    private final OrderItemRepository orderItemRepository;

    @Transactional//(readOnly = true)
    public List<OrderItemStatusUpdateDTO> getKitchenItemsByCategory(String categoryName) {
        List<OrderItemEntity> items = orderItemRepository.findByMenu_Category_NameAndStatus(
                categoryName, OrderItemStatusEnum.PREPARING
        );

        return items.stream()
                .map(OrderItemMapper::toDTO)
                .collect(Collectors.toList());
    }
//
//    @Transactional//(readOnly = true)
//    public List<KitchenItemDTO> getReadyToServeItems() {
//        return orderItemRepository.findAllByStatus(OrderItemStatusEnum.READY_TO_SERVE)
//                .stream()
//                .map(this::toKitchenItemDTO)
//                .collect(Collectors.toList());
//    }

//    private KitchenItemDTO toKitchenItemDTO(OrderItemEntity item) {
//        return KitchenItemDTO.builder()
//                .id(item.getId())
//                .menuName(item.getMenu().getName())
//                .quantity(item.getQuantity())
//                .status(item.getStatus().name())
//                .tableNumber(item.getOrder().getTable() != null
//                        ? item.getOrder().getTable().getTableNumber()
//                        : "Takeaway")
//                .build();
//    }
//    private KitchenItemDTO toKitchenItemDTO(OrderItemEntity item) {
//        KitchenItemDTO dto = new KitchenItemDTO();
//        dto.setId(item.getId());
//        dto.setMenuName(item.getMenu().getName());
//        dto.setQuantity(item.getQuantity());
//        dto.setStatus(item.getStatus().name());
//        dto.setTableNumber(item.getOrder().getTable() != null
//                ? item.getOrder().getTable().getTableNumber()
//                : "Takeaway");
//        return dto;
//    }
}