package priorsolution.training.project1.restaurant_management_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import priorsolution.training.project1.restaurant_management_app.Response.KitchenManagementResponse;
import priorsolution.training.project1.restaurant_management_app.entity.OrderEntity;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderStatusEnum;
import priorsolution.training.project1.restaurant_management_app.exception.ResourceNotFoundException;
import priorsolution.training.project1.restaurant_management_app.mapper.KitchenManagementMapper;
import priorsolution.training.project1.restaurant_management_app.repository.KitchenManagementRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KitchenManagementService {
    private final   KitchenManagementRepository kitchenManagementRepository;

    @Transactional(readOnly = true)
    public List<KitchenManagementResponse> getAllActiveOrders() {

        List<OrderEntity> entities = kitchenManagementRepository.findAllByStatus(OrderStatusEnum.ACTIVE);

        if (entities.isEmpty()) {
            throw new ResourceNotFoundException("No active orders found", "ACTIVE_ORDERS_NOT_FOUND");
        }
        return entities.stream()
                .map(KitchenManagementMapper::toInfoResponse)
                .toList();
    }

}
