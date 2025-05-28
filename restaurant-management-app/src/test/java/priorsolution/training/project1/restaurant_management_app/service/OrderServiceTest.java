package priorsolution.training.project1.restaurant_management_app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import priorsolution.training.project1.restaurant_management_app.dto.*;
import priorsolution.training.project1.restaurant_management_app.entity.*;
import priorsolution.training.project1.restaurant_management_app.entity.enums.*;
import priorsolution.training.project1.restaurant_management_app.exception.*;
import priorsolution.training.project1.restaurant_management_app.mapper.*;
import priorsolution.training.project1.restaurant_management_app.repository.*;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private TableInfoService tableInfoService;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ ทดสอบ: สร้างออเดอร์ใหม่แบบ DINE_IN และยังไม่มีออเดอร์ ACTIVE ในโต๊ะนั้น
    @Test
    void createOrder_whenDineInAndNoActiveOrder_shouldCreateNewOrder() {
        // Arrange
        OrderItemRequestDTO itemDto = new OrderItemRequestDTO();
        itemDto.setMenuId(1L);
        itemDto.setQuantity(2);

        OrderRequestDTO dto = OrderRequestDTO.builder()
                .orderType(OrderTypeEnum.DINE_IN)
                .tableId(10L)
                .items(List.of(itemDto))
                .build();

        MenuEntity menu = new MenuEntity();
        menu.setId(1L);
        menu.setPrice(BigDecimal.valueOf(100));

        OrderItemEntity itemEntity = new OrderItemEntity();
        itemEntity.setMenu(menu);
        itemEntity.setQuantity(2);
        itemEntity.setPrice(menu.getPrice().multiply(BigDecimal.valueOf(2)));

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderItems(List.of(itemEntity));

        OrderEntity savedEntity = new OrderEntity();
        savedEntity.setId(1L);
        savedEntity.setOrderType(OrderTypeEnum.DINE_IN);

        OrderResponseDTO responseDTO = new OrderResponseDTO();
        responseDTO.setId(1L);

        // Mock dependencies
        when(orderRepository.findByTableIdAndStatus(10L, OrderStatusEnum.ACTIVE)).thenReturn(Optional.empty());
        when(orderMapper.toEntity(dto)).thenReturn(orderEntity);
        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));
        when(orderRepository.save(orderEntity)).thenReturn(savedEntity);
        when(orderMapper.toDto(savedEntity)).thenReturn(responseDTO);
        when(tableInfoService.getEntityById(10L)).thenReturn(new TableInfoEntity());
        doNothing().when(tableInfoService).setTableStatus(10L, TableStatusEnum.OCCUPIED);

        // Act
        OrderResponseDTO result = orderService.createOrder(dto);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
    }

    // ❌ ทดสอบ: กรณีส่ง items เป็น list ว่าง → ควรโยน BadRequestException
    @Test
    void createOrder_whenItemsEmpty_shouldThrowBadRequestException() {
        OrderRequestDTO dto = OrderRequestDTO.builder()
                .orderType(OrderTypeEnum.TAKEAWAY)
                .items(Collections.emptyList())
                .build();

        assertThatThrownBy(() -> orderService.createOrder(dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Order items cannot be empty");
    }

    // ❌ ทดสอบ: กรณีที่ orderMapper คืนค่า null → ควรเกิด NullPointerException
    @Test
    void createOrder_whenMapperReturnsNull_shouldThrowNullPointerException() {
        OrderItemRequestDTO itemDto = new OrderItemRequestDTO();
        itemDto.setMenuId(1L);
        itemDto.setQuantity(1);

        OrderRequestDTO dto = OrderRequestDTO.builder()
                .orderType(OrderTypeEnum.TAKEAWAY)
                .items(List.of(itemDto))
                .build();

        when(orderMapper.toEntity(dto)).thenReturn(null);

        assertThatThrownBy(() -> orderService.createOrder(dto))
                .isInstanceOf(NullPointerException.class);
    }

    // ❌ ทดสอบ: เมื่อ Repository โยน Exception (เช่น DB error) → ควรโยน RuntimeException
    @Test
    void createOrder_whenRepositoryThrowsException_shouldThrowRuntimeException() {
        OrderItemRequestDTO itemDto = new OrderItemRequestDTO();
        itemDto.setMenuId(1L);
        itemDto.setQuantity(1);

        OrderRequestDTO dto = OrderRequestDTO.builder()
                .orderType(OrderTypeEnum.TAKEAWAY)
                .items(List.of(itemDto))
                .build();

        MenuEntity menu = new MenuEntity();
        menu.setId(1L);

        OrderItemEntity itemEntity = new OrderItemEntity();
        itemEntity.setMenu(menu);
        itemEntity.setQuantity(1);

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderItems(List.of(itemEntity));
        orderEntity.setTotalPrice(BigDecimal.ZERO);

        when(orderMapper.toEntity(dto)).thenReturn(orderEntity);
        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));
        when(orderRepository.save(any())).thenThrow(new RuntimeException("DB Error"));

        assertThatThrownBy(() -> orderService.createOrder(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("DB Error");
    }

    // ❌ ทดสอบ: กรณี Repository คืนค่า null แทน list → ควรโยน NullPointerException
    @Test
    void getAllOrders_whenRepositoryReturnsNull_shouldThrowNullPointerException() {
        when(orderRepository.findAll()).thenReturn(null);

        assertThatThrownBy(() -> orderService.getAllOrders())
                .isInstanceOf(NullPointerException.class);
    }

    // ❌ ทดสอบ: กรณีไม่มีรายการออเดอร์ในระบบ → ควรโยน ResourceNotFoundException


    // ✅ ทดสอบ: สร้างออเดอร์แบบ DINE_IN และพบว่าโต๊ะนั้นมีออเดอร์ ACTIVE อยู่แล้ว → ควรเพิ่มรายการเข้าออเดอร์เดิม
    @Test
    void createOrder_whenDineInAndActiveOrderExists_shouldAddItemsToExistingOrder() {
        OrderItemRequestDTO itemDto = new OrderItemRequestDTO();
        itemDto.setMenuId(1L);
        itemDto.setQuantity(1);

        OrderRequestDTO dto = OrderRequestDTO.builder()
                .orderType(OrderTypeEnum.DINE_IN)
                .tableId(10L)
                .items(List.of(itemDto))
                .build();

        MenuEntity menu = new MenuEntity();
        menu.setId(1L);
        menu.setName("ข้าวผัด");
        menu.setPrice(BigDecimal.valueOf(100));

        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));

        OrderEntity existingOrder = new OrderEntity();
        existingOrder.setOrderItems(new ArrayList<>());
        existingOrder.setStatus(OrderStatusEnum.ACTIVE);
        existingOrder.setOrderType(OrderTypeEnum.DINE_IN);

        when(orderRepository.findByTableIdAndStatus(10L, OrderStatusEnum.ACTIVE))
                .thenReturn(Optional.of(existingOrder));

        OrderEntity saved = new OrderEntity();
        saved.setId(99L);
        saved.setOrderItems(existingOrder.getOrderItems());
        saved.setOrderType(OrderTypeEnum.DINE_IN);
        saved.setStatus(OrderStatusEnum.ACTIVE);

        when(orderRepository.save(any())).thenReturn(saved);

        OrderResponseDTO expectedResponse = new OrderResponseDTO();
        expectedResponse.setId(99L);
        when(orderMapper.toDto(saved)).thenReturn(expectedResponse);

        // Act
        OrderResponseDTO response = orderService.createOrder(dto);

        // Assert
        assertThat(response.getId()).isEqualTo(99L);
    }

    // ❌ ทดสอบ: เมื่อไม่พบเมนูตาม menuId ที่ส่งมา → ควรโยน ResourceNotFoundException
    @Test
    void createOrder_whenMenuNotFound_shouldThrowResourceNotFoundException() {
        OrderItemRequestDTO itemDto = new OrderItemRequestDTO();
        itemDto.setMenuId(999L);
        itemDto.setQuantity(1);

        OrderRequestDTO dto = OrderRequestDTO.builder()
                .orderType(OrderTypeEnum.TAKEAWAY)
                .items(List.of(itemDto))
                .build();

        MenuEntity dummyMenu = new MenuEntity();
        dummyMenu.setId(999L);

        OrderItemEntity itemEntity = new OrderItemEntity();
        itemEntity.setMenu(dummyMenu);
        itemEntity.setQuantity(1);

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderItems(List.of(itemEntity));

        when(orderMapper.toEntity(dto)).thenReturn(orderEntity);
        when(menuRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Menu with ID 999 not found");
    }

}
