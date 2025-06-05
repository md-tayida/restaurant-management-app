package priorsolution.training.project1.restaurant_management_app.service;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import priorsolution.training.project1.restaurant_management_app.dto.OrderItemStatusUpdateDTO;
import priorsolution.training.project1.restaurant_management_app.entity.OrderEntity;
import priorsolution.training.project1.restaurant_management_app.entity.OrderItemEntity;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderItemStatusEnum;
import priorsolution.training.project1.restaurant_management_app.exception.ResourceNotFoundException;
import priorsolution.training.project1.restaurant_management_app.mapper.OrderItemMapper;
import priorsolution.training.project1.restaurant_management_app.repository.OrderItemRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderItemServiceTest {

    private final OrderItemRepository orderItemRepository = mock(OrderItemRepository.class);
    private final OrderService orderService = mock(OrderService.class);
    private final OrderItemService orderItemService = new OrderItemService(orderItemRepository, orderService);

    // ทดสอบกรณี: ดึงรายการอาหารที่อยู่ในสถานะ PREPARING แล้วพบข้อมูล
    @Test
    void getAllPreparingItems_whenItemsExist_shouldReturnList() {
        OrderItemEntity entity = new OrderItemEntity();
        entity.setStatus(OrderItemStatusEnum.PREPARING);

        OrderItemStatusUpdateDTO dto = OrderItemStatusUpdateDTO.builder()
                .id(1L)
                .createTime(LocalDateTime.now())
                .tableNumber("A1")
                .menuName("ข้าวผัด")
                .quantity(2)
                .status(OrderItemStatusEnum.PREPARING)
                .build();

        when(orderItemRepository.findAllByStatus(OrderItemStatusEnum.PREPARING)).thenReturn(List.of(entity));
        try (var mocked = Mockito.mockStatic(OrderItemMapper.class)) {
            mocked.when(() -> OrderItemMapper.toDTO(entity)).thenReturn(dto);

            List<OrderItemStatusUpdateDTO> result = orderItemService.getAllPreparingItems();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus()).isEqualTo(OrderItemStatusEnum.PREPARING);
        }
    }

    // ทดสอบกรณี: ไม่พบรายการอาหารที่อยู่ในสถานะ PREPARING
    @Test
    void getAllPreparingItems_whenNoItemsFound_shouldThrowResourceNotFoundException() {
        when(orderItemRepository.findAllByStatus(OrderItemStatusEnum.PREPARING)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> orderItemService.getAllPreparingItems())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No preparing items found");
    }

    // ทดสอบกรณี: การแปลงข้อมูล (mapping) ล้มเหลว
    @Test
    void getAllPreparingItems_whenMapperFails_shouldThrowException() {
        OrderItemEntity entity = new OrderItemEntity();
        entity.setStatus(OrderItemStatusEnum.PREPARING);

        when(orderItemRepository.findAllByStatus(OrderItemStatusEnum.PREPARING)).thenReturn(List.of(entity));

        try (var mocked = Mockito.mockStatic(OrderItemMapper.class)) {
            mocked.when(() -> OrderItemMapper.toDTO(entity)).thenThrow(new RuntimeException("Mapping error"));

            assertThatThrownBy(() -> orderItemService.getAllPreparingItems())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Mapping error");
        }
    }

    // ทดสอบกรณี: ดึงรายการอาหารจากหมวดหมู่ที่กำหนดแล้วพบข้อมูล
    @Test
    void getAllPreparingItemsByCategory_whenItemsExist_shouldReturnList() {
        OrderItemEntity entity = new OrderItemEntity();
        entity.setStatus(OrderItemStatusEnum.PREPARING);

        OrderItemStatusUpdateDTO dto = OrderItemStatusUpdateDTO.builder()
                .id(1L)
                .menuName("ผัดไทย")
                .status(OrderItemStatusEnum.PREPARING)
                .build();

        when(orderItemRepository.findByMenu_Category_NameAndStatus("Food", OrderItemStatusEnum.PREPARING))
                .thenReturn(List.of(entity));

        try (MockedStatic<OrderItemMapper> mocked = Mockito.mockStatic(OrderItemMapper.class)) {
            mocked.when(() -> OrderItemMapper.toDTO(entity)).thenReturn(dto);

            List<OrderItemStatusUpdateDTO> result = orderItemService.getAllPreparingItems("Food");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getMenuName()).isEqualTo("ผัดไทย");
        }
    }

    // ทดสอบกรณี: ไม่พบรายการอาหารในหมวดหมู่ที่กำหนด
    @Test
    void getAllPreparingItemsByCategory_whenNoItemsFound_shouldThrowResourceNotFoundException() {
        when(orderItemRepository.findByMenu_Category_NameAndStatus("Dessert", OrderItemStatusEnum.PREPARING))
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> orderItemService.getAllPreparingItems("Dessert"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("category: Dessert");
    }

    // ทดสอบกรณี: ดึงรายการอาหารที่พร้อมเสิร์ฟแล้วพบข้อมูล
    @Test
    void getReadyToServeItems_whenItemsExist_shouldReturnList() {
        OrderItemEntity entity = new OrderItemEntity();
        entity.setStatus(OrderItemStatusEnum.READY_TO_SERVE);

        OrderItemStatusUpdateDTO dto = OrderItemStatusUpdateDTO.builder()
                .id(2L)
                .status(OrderItemStatusEnum.READY_TO_SERVE)
                .menuName("สเต๊ก")
                .build();

        when(orderItemRepository.findAllByStatus(OrderItemStatusEnum.READY_TO_SERVE))
                .thenReturn(List.of(entity));

        try (MockedStatic<OrderItemMapper> mocked = Mockito.mockStatic(OrderItemMapper.class)) {
            mocked.when(() -> OrderItemMapper.toDTO(entity)).thenReturn(dto);

            List<OrderItemStatusUpdateDTO> result = orderItemService.getReadyToServeItems();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus()).isEqualTo(OrderItemStatusEnum.READY_TO_SERVE);
        }
    }

    // ทดสอบกรณี: ไม่พบรายการอาหารที่พร้อมเสิร์ฟ
    @Test
    void getReadyToServeItems_whenNoItemsFound_shouldThrowResourceNotFoundException() {
        when(orderItemRepository.findAllByStatus(OrderItemStatusEnum.READY_TO_SERVE))
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> orderItemService.getReadyToServeItems())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("ready-to-serve items");
    }

    // ทดสอบกรณี: อัปเดตสถานะเป็น READY_TO_SERVE สำเร็จ
    @Test
    void markItemReadyToServe_whenItemExists_shouldUpdateStatusToReadyToServe() {
        Long itemId = 1L;
        OrderItemEntity entity = new OrderItemEntity();
        entity.setStatus(OrderItemStatusEnum.PREPARING);

        when(orderItemRepository.findById(itemId)).thenReturn(Optional.of(entity));
        when(orderItemRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        orderItemService.markItemReadyToServe(itemId);

        assertThat(entity.getStatus()).isEqualTo(OrderItemStatusEnum.READY_TO_SERVE);
        verify(orderItemRepository).save(entity);
    }

    // ทดสอบกรณี: ไม่พบรายการอาหารที่ต้องอัปเดตเป็น READY_TO_SERVE
    @Test
    void markItemReadyToServe_whenItemNotFound_shouldThrowResourceNotFoundException() {
        when(orderItemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderItemService.markItemReadyToServe(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Item not found with id: 1");
    }

    // ทดสอบกรณี: บันทึกข้อมูลไม่สำเร็จ (Database error)
    @Test
    void markItemReadyToServe_whenSaveFails_shouldThrowException() {
        Long itemId = 1L;
        OrderItemEntity entity = new OrderItemEntity();
        entity.setStatus(OrderItemStatusEnum.PREPARING);

        when(orderItemRepository.findById(itemId)).thenReturn(Optional.of(entity));
        when(orderItemRepository.save(any())).thenThrow(new RuntimeException("DB error"));

        assertThatThrownBy(() -> orderItemService.markItemReadyToServe(itemId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("DB error");
    }

    // ทดสอบกรณี: อัปเดตรายการอาหารให้ DONE สำเร็จ
    @Test
    void markItemDone_whenItemExists_shouldUpdateStatusToDone() {
        OrderItemEntity item = new OrderItemEntity();
        item.setStatus(OrderItemStatusEnum.READY_TO_SERVE);

        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(orderItemRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        orderItemService.markItemDone(1L);

        assertThat(item.getStatus()).isEqualTo(OrderItemStatusEnum.DONE);
        verify(orderItemRepository).save(item);
    }

    // ทดสอบกรณี: ไม่พบรายการอาหารที่ต้องทำให้ DONE
    @Test
    void markItemDone_whenItemNotFound_shouldThrowResourceNotFoundException() {
        when(orderItemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderItemService.markItemDone(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Item not found with id: 1");
    }

    // ทดสอบกรณี: ยกเลิกรายการอาหารสำเร็จ และเรียกให้คำนวณราคาทั้งหมดใหม่
    @Test
    void cancelOrderItem_whenItemIsValid_shouldUpdateStatusAndRecalculatePrice() {
        Long itemId = 1L;
        OrderEntity order = new OrderEntity();
        order.setId(10L);

        OrderItemEntity entity = new OrderItemEntity();
        entity.setId(itemId);
        entity.setStatus(OrderItemStatusEnum.PREPARING);
        entity.setOrder(order);

        when(orderItemRepository.findById(itemId)).thenReturn(Optional.of(entity));
        when(orderItemRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        orderItemService.cancelOrderItem(itemId);

        assertThat(entity.getStatus()).isEqualTo(OrderItemStatusEnum.CANCELED);
        verify(orderItemRepository).save(entity);
        verify(orderService).recalculateTotalPrice(order.getId());
    }

    // ทดสอบกรณี: ไม่พบรายการอาหารที่ต้องการยกเลิก
    @Test
    void cancelOrderItem_whenItemNotFound_shouldThrowResourceNotFoundException() {
        when(orderItemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderItemService.cancelOrderItem(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Item not found");
    }

    // ทดสอบกรณี: รายการอาหารถูกยกเลิกไปแล้ว ไม่ควรยกเลิกซ้ำ
    @Test
    void cancelOrderItem_whenItemStatusInvalid_shouldThrowOrIgnore() {
        Long itemId = 1L;
        OrderItemEntity entity = new OrderItemEntity();
        entity.setId(itemId);
        entity.setStatus(OrderItemStatusEnum.CANCELED); // already canceled

        when(orderItemRepository.findById(itemId)).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> orderItemService.cancelOrderItem(itemId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot cancel item that is already canceled");

        verify(orderItemRepository, never()).save(any());
        verify(orderService, never()).recalculateTotalPrice(any());
    }

    // ทดสอบกรณี: รายการถูกยกเลิกแล้ว
    @Test
    void cancelOrderItem_whenItemAlreadyCanceled_shouldThrowIllegalStateException() {
        OrderItemEntity item = new OrderItemEntity();
        item.setStatus(OrderItemStatusEnum.CANCELED);

        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> orderItemService.cancelOrderItem(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already canceled");
    }

    // ทดสอบกรณี: ยกเลิกไม่ได้เพราะไม่ผูกกับออเดอร์
    @Test
    void cancelOrderItem_whenOrderReferenceIsNull_shouldThrowIllegalStateException() {
        OrderItemEntity item = new OrderItemEntity();
        item.setStatus(OrderItemStatusEnum.PREPARING);
        item.setOrder(null); // ❗ สำคัญ

        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> orderItemService.cancelOrderItem(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Order reference is null");
    }
}
