package priorsolution.training.project1.restaurant_management_app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import priorsolution.training.project1.restaurant_management_app.Response.OrderItemResponse;
import priorsolution.training.project1.restaurant_management_app.entity.OrderItemEntity;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderItemStatusEnum;
import priorsolution.training.project1.restaurant_management_app.exception.ResourceNotFoundException;
import priorsolution.training.project1.restaurant_management_app.repository.OrderItemRepository;
import priorsolution.training.project1.restaurant_management_app.request.OrderItemStatusRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderItemServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;


    @InjectMocks
    private OrderItemService orderItemService;

    private OrderItemEntity orderItem;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        orderItem = new OrderItemEntity();
        orderItem.setId(1L);
        orderItem.setStatus(OrderItemStatusEnum.PENDING);
    }

    @Test
    void updateOrderItemStatus_shouldUpdateSuccessfully_whenValidTransition() {

        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(orderItem));
        when(orderItemRepository.save(any(OrderItemEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderItemStatusRequest request = new OrderItemStatusRequest();
        request.setOrderItemStatus(OrderItemStatusEnum.IN_PROGRESS);

        OrderItemResponse result = orderItemService.updateOrderItemStatus(1L, request);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OrderItemStatusEnum.IN_PROGRESS);

        verify(orderItemRepository, times(1)).findById(1L);
        verify(orderItemRepository, times(1)).save(any(OrderItemEntity.class));
    }

    @Test
    void updateOrderItemStatus_shouldThrowException_whenNotFound() {

        when(orderItemRepository.findById(99L)).thenReturn(Optional.empty());
        OrderItemStatusRequest request = new OrderItemStatusRequest();
        request.setOrderItemStatus(OrderItemStatusEnum.IN_PROGRESS);

        assertThatThrownBy(() -> orderItemService.updateOrderItemStatus(99L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order item not found");

        verify(orderItemRepository, times(1)).findById(99L);
        verify(orderItemRepository, never()).save(any());
    }

    @Test
    void updateOrderItemStatus_shouldThrowException_whenInvalidTransition() {
        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(orderItem));

        OrderItemStatusRequest request = new OrderItemStatusRequest();
        request.setOrderItemStatus(OrderItemStatusEnum.READY);

        assertThatThrownBy(() -> orderItemService.updateOrderItemStatus(1L, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Invalid status transition");

        verify(orderItemRepository, times(1)).findById(1L);
        verify(orderItemRepository, never()).save(any());
    }
}
