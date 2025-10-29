package priorsolution.training.project1.restaurant_management_app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import priorsolution.training.project1.restaurant_management_app.Response.OrderResponse;
import priorsolution.training.project1.restaurant_management_app.entity.*;
import priorsolution.training.project1.restaurant_management_app.entity.enums.*;
import priorsolution.training.project1.restaurant_management_app.exception.ResourceNotFoundException;
import priorsolution.training.project1.restaurant_management_app.repository.*;
import priorsolution.training.project1.restaurant_management_app.request.OrderItemRequest;
import priorsolution.training.project1.restaurant_management_app.request.OrderRequest;
import priorsolution.training.project1.restaurant_management_app.request.OrderStatusRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private TableInfoRepository tableInfoRepository;

    @Mock
    private TableInfoService tableInfoService;

    @InjectMocks
    private OrderService orderService;

    private TableInfoEntity table;
    private OrderEntity order;
    private OrderRequest request;

    @BeforeEach
    void setUp() {
        table = new TableInfoEntity();
        table.setId(1L);
        table.setStatus(TableStatusEnum.AVAILABLE);

        order = new OrderEntity();
        order.setId(10L);
        order.setStatus(OrderStatusEnum.ACTIVE);
        order.setOrderType(OrderTypeEnum.DINE_IN);
        order.setTable(table);

        request = new OrderRequest();
        request.setOrderType(OrderTypeEnum.DINE_IN);
        request.setTableId(1L);
    }

    @Test
    void getAllOrders_shouldReturnList_whenDataExists() {
        when(orderRepository.findAll()).thenReturn(List.of(order));

        List<OrderResponse> result = orderService.getAllOrders();

        assertThat(result).isNotEmpty();
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void getAllOrders_shouldThrowException_whenNoOrdersFound() {
        when(orderRepository.findAll()).thenReturn(List.of());

        assertThatThrownBy(() -> orderService.getAllOrders())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No tables found");

        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void createOrder_shouldCreateOrder_whenTableAvailable() {
        when(tableInfoRepository.findById(1L)).thenReturn(Optional.of(table));

        MenuEntity menu = new MenuEntity();
        menu.setId(100L);
        menu.setName("ข้าวผัดหมู");
        menu.setPrice(BigDecimal.valueOf(50.0));
        when(menuRepository.findById(100L)).thenReturn(Optional.of(menu));

        OrderRequest request = new OrderRequest();
        request.setOrderType(OrderTypeEnum.DINE_IN);
        request.setTableId(1L);

        OrderItemRequest itemReq = new OrderItemRequest();
        itemReq.setMenuId(100L);
        itemReq.setQuantity(2);
        itemReq.setDescription("ไม่ใส่พริก");

        request.setOrderItems(List.of(itemReq));
        OrderEntity savedOrder = new OrderEntity();
        savedOrder.setId(10L);
        savedOrder.setOrderType(OrderTypeEnum.DINE_IN);
        savedOrder.setTable(table);

        when(orderRepository.save(any(OrderEntity.class))).thenReturn(savedOrder);
        OrderResponse result = orderService.createOrder(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);

        verify(tableInfoRepository, times(1)).findById(1L);
        verify(tableInfoRepository, times(1)).save(any(TableInfoEntity.class));
        verify(orderRepository, times(1)).save(any(OrderEntity.class));
        verify(menuRepository, times(1)).findById(100L);
    }


    @Test
    void createOrder_shouldThrowException_whenTableNotFound() {
        when(tableInfoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Table not found");
    }

    @Test
    void createOrder_shouldThrowException_whenTableNotAvailable() {
        table.setStatus(TableStatusEnum.OCCUPIED);
        when(tableInfoRepository.findById(1L)).thenReturn(Optional.of(table));

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Table is not available");
    }

    @Test
    void updateOrderStatus_shouldCompleteOrder_whenAllItemsServed() {
        order.setStatus(OrderStatusEnum.ACTIVE);
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrderId(10L)).thenReturn(List.of(
                createOrderItem(OrderItemStatusEnum.SERVED),
                createOrderItem(OrderItemStatusEnum.CANCELED)
        ));
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(order);

        OrderStatusRequest req = new OrderStatusRequest();
        req.setOrderStatus(OrderStatusEnum.COMPLETED);

        OrderResponse result = orderService.updateOrderStatus(10L, req);

        assertThat(result).isNotNull();
        verify(tableInfoRepository, times(1)).save(any(TableInfoEntity.class));
        verify(orderRepository, times(1)).save(any(OrderEntity.class));
    }

    @Test
    void updateOrderStatus_shouldThrow_whenUnservedItemsExist() {
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrderId(10L)).thenReturn(List.of(
                createOrderItem(OrderItemStatusEnum.PENDING)
        ));

        OrderStatusRequest req = new OrderStatusRequest();
        req.setOrderStatus(OrderStatusEnum.COMPLETED);

        assertThatThrownBy(() -> orderService.updateOrderStatus(10L, req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot complete order");

        verify(orderRepository, never()).save(any());
    }

    @Test
    void updateOrderStatus_shouldThrow_whenOrderNotFound() {
        when(orderRepository.findById(10L)).thenReturn(Optional.empty());

        OrderStatusRequest req = new OrderStatusRequest();
        req.setOrderStatus(OrderStatusEnum.CANCELED);

        assertThatThrownBy(() -> orderService.updateOrderStatus(10L, req))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order not found");
    }

    @Test
    void updateOrderStatus_shouldMarkTableAvailable_whenOrderCanceled() {
        order.setOrderType(OrderTypeEnum.DINE_IN);
        order.setTable(table);

        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrderId(10L)).thenReturn(List.of());
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(order);

        OrderStatusRequest req = new OrderStatusRequest();
        req.setOrderStatus(OrderStatusEnum.CANCELED);

        OrderResponse result = orderService.updateOrderStatus(10L, req);

        assertThat(result).isNotNull();
        verify(tableInfoRepository, times(1)).save(table);
    }

    private OrderItemEntity createOrderItem(OrderItemStatusEnum status) {
        OrderItemEntity item = new OrderItemEntity();
        item.setStatus(status);
        return item;
    }
}
