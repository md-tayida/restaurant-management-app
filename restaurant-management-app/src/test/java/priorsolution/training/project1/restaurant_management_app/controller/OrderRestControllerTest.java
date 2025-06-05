package priorsolution.training.project1.restaurant_management_app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import priorsolution.training.project1.restaurant_management_app.dto.OrderItemRequestDTO;
import priorsolution.training.project1.restaurant_management_app.dto.OrderRequestDTO;
import priorsolution.training.project1.restaurant_management_app.dto.OrderResponseDTO;
import priorsolution.training.project1.restaurant_management_app.entity.OrderEntity;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderStatusEnum;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderTypeEnum;
import priorsolution.training.project1.restaurant_management_app.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;


// แทนที่ด้วย
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderRestControllerTest {


    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;

    @Autowired
    public OrderRestControllerTest(MockMvc mockMvc,
                                   ObjectMapper objectMapper,
                                   OrderRepository orderRepository) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.orderRepository = orderRepository;
    }

    @BeforeEach
    void cleanDb() {
        orderRepository.deleteAll(); // ล้างข้อมูลก่อนแต่ละเทสต์
    }

    @Test
    void testGetAllOrders_whenNoOrders_shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]")); // คาดว่าจะได้ list ว่าง
    }

    @Test
    void testCreateOrder_andGetOrder() throws Exception {
        // เตรียม OrderRequestDTO สำหรับสร้างออเดอร์
        OrderItemRequestDTO itemDTO = OrderItemRequestDTO.builder()
                .menuId(3L)
                .quantity(2)
                .build();

        OrderRequestDTO orderRequest = OrderRequestDTO.builder()
                .orderType(OrderTypeEnum.TAKEAWAY)
                .items(List.of(itemDTO))
                .build();

        // เรียก POST /api/orders สร้างออเดอร์
        MvcResult postResult = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        String jsonResponse = postResult.getResponse().getContentAsString();
        OrderResponseDTO responseDTO = objectMapper.readValue(jsonResponse, OrderResponseDTO.class);

        // เรียก GET /api/orders ต้องเจอออเดอร์ที่สร้าง
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(responseDTO.getId()));
    }

    @Test
    void testCancelOrder() throws Exception {
        // สร้างออเดอร์ล่วงหน้าใน DB (อาจจะเรียก service หรือ repository ตรงนี้)
        OrderEntity order = new OrderEntity();
        order.setOrderType(OrderTypeEnum.TAKEAWAY);
        order.setStatus(OrderStatusEnum.ACTIVE);
        order.setOrderItems(new ArrayList<>());
        order = orderRepository.save(order);

        // เรียก PUT /api/orders/{orderId}/cancel
        mockMvc.perform(put("/api/orders/" + order.getId() + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId()))
                .andExpect(jsonPath("$.status").value(OrderStatusEnum.CANCELED.name()));
    }

    @Test
    void testPayOrder() throws Exception {
        // สร้างออเดอร์ล่วงหน้าใน DB
        OrderEntity order = new OrderEntity();
        order.setOrderType(OrderTypeEnum.TAKEAWAY);
        order.setStatus(OrderStatusEnum.ACTIVE);
        order.setOrderItems(new ArrayList<>());
        order = orderRepository.save(order);

        // เรียก POST /api/orders/{orderId}/pay
        mockMvc.perform(post("/api/orders/" + order.getId() + "/pay"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId()))
                .andExpect(jsonPath("$.status").value(OrderStatusEnum.PAID.name()));
    }
}
