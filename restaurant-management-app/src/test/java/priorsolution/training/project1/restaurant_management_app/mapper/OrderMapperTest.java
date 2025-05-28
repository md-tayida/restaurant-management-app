package priorsolution.training.project1.restaurant_management_app.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import priorsolution.training.project1.restaurant_management_app.dto.OrderItemRequestDTO;
import priorsolution.training.project1.restaurant_management_app.dto.OrderRequestDTO;
import priorsolution.training.project1.restaurant_management_app.entity.MenuEntity;
import priorsolution.training.project1.restaurant_management_app.entity.OrderEntity;
import priorsolution.training.project1.restaurant_management_app.entity.TableInfoEntity;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderStatusEnum;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderTypeEnum;
import priorsolution.training.project1.restaurant_management_app.exception.ResourceNotFoundException;
import priorsolution.training.project1.restaurant_management_app.repository.MenuRepository;
import priorsolution.training.project1.restaurant_management_app.service.TableInfoService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)

class OrderMapperTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private TableInfoService tableInfoService;

    @InjectMocks
    private OrderMapper orderMapper;

    // กรณีมี tableId -> ควร map table ได้ถูกต้องและมี order item
    @Test
    void toEntity_whenDineInWithValidMenuAndTable_shouldMapCorrectly() {
        MenuEntity menu = new MenuEntity();
        menu.setId(1L);
        menu.setName("ข้าวขาหมู");
        menu.setPrice(BigDecimal.valueOf(100));

        TableInfoEntity table = new TableInfoEntity();
        table.setId(10L);

        OrderItemRequestDTO itemDTO = OrderItemRequestDTO.builder()
                .menuId(1L)
                .quantity(2)
                .build();

        OrderRequestDTO dto = OrderRequestDTO.builder()
                .orderType(OrderTypeEnum.DINE_IN)
                .tableId(10L)
                .items(List.of(itemDTO))
                .build();

        Mockito.when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));
        Mockito.when(tableInfoService.getEntityById(10L)).thenReturn(table);

        OrderEntity order = orderMapper.toEntity(dto);

        assertEquals(OrderTypeEnum.DINE_IN, order.getOrderType());
        assertEquals(OrderStatusEnum.ACTIVE, order.getStatus());
        assertEquals(1, order.getOrderItems().size());
        assertEquals(menu, order.getOrderItems().get(0).getMenu());
        assertEquals(table, order.getTable());
    }

    // กรณีไม่มี tableId -> ควรไม่เรียก tableService และไม่ set table
    @Test
    void toEntity_whenTakeawayWithoutTable_shouldNotCallTableInfoService() {
        MenuEntity menu = new MenuEntity();
        menu.setId(2L);
        menu.setName("ข้าวมันไก่");
        menu.setPrice(BigDecimal.valueOf(50));

        OrderItemRequestDTO itemDTO = OrderItemRequestDTO.builder()
                .menuId(2L)
                .quantity(1)
                .build();

        OrderRequestDTO dto = OrderRequestDTO.builder()
                .orderType(OrderTypeEnum.TAKEAWAY)
                .tableId(null)
                .items(List.of(itemDTO))
                .build();

        Mockito.when(menuRepository.findById(2L)).thenReturn(Optional.of(menu));

        OrderEntity order = orderMapper.toEntity(dto);

        assertEquals(OrderTypeEnum.TAKEAWAY, order.getOrderType());
        assertNull(order.getTable());
    }

    // กรณีหาเมนูไม่เจอ -> ควร throw ResourceNotFoundException
    @Test
    void toEntity_whenMenuNotFound_shouldThrowException() {
        OrderItemRequestDTO itemDTO = OrderItemRequestDTO.builder()
                .menuId(99L)
                .quantity(1)
                .build();

        OrderRequestDTO dto = OrderRequestDTO.builder()
                .orderType(OrderTypeEnum.DINE_IN)
                .items(List.of(itemDTO))
                .build();

        Mockito.when(menuRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderMapper.toEntity(dto));
    }

    // กรณีหาโต๊ะไม่เจอ -> ควร throw ResourceNotFoundException
    @Test
    void toEntity_whenTableNotFound_shouldThrowException() {
        OrderItemRequestDTO itemDTO = OrderItemRequestDTO.builder()
                .menuId(1L)
                .quantity(1)
                .build();

        OrderRequestDTO dto = OrderRequestDTO.builder()
                .orderType(OrderTypeEnum.DINE_IN)
                .tableId(99L)
                .items(List.of(itemDTO))
                .build();

        Mockito.when(tableInfoService.getEntityById(99L))
                .thenThrow(new ResourceNotFoundException("Table not found", "NOT_FOUND"));

        assertThrows(ResourceNotFoundException.class, () -> orderMapper.toEntity(dto));
    }

    // กรณีไม่มีรายการ order item -> ควร return order ที่ไม่มี items
    @Test
    void toEntity_whenItemsAreEmpty_shouldReturnOrderWithNoItems() {
        OrderRequestDTO dto = OrderRequestDTO.builder()
                .orderType(OrderTypeEnum.TAKEAWAY)
                .items(List.of())
                .build();

        OrderEntity order = orderMapper.toEntity(dto);

        assertNotNull(order);
        assertEquals(0, order.getOrderItems().size());
    }
}

