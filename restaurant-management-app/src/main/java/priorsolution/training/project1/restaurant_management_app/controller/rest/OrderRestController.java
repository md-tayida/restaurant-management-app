package priorsolution.training.project1.restaurant_management_app.controller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import priorsolution.training.project1.restaurant_management_app.dto.OrderRequestDTO;

import priorsolution.training.project1.restaurant_management_app.dto.OrderResponseDTO;

import priorsolution.training.project1.restaurant_management_app.service.OrderService;

import java.net.URI;
import java.util.List;


@RestController

@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderRestController {

    private final OrderService orderService;
   // private final OrderResponseDTO orderResponseDTO;

    @GetMapping  //ดูเมนูทั้งหมด
    public List<OrderResponseDTO> getAllOrders() {
        return orderService.getAllOrders(); // ไ
    }
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO dto) {
        OrderResponseDTO responseDTO = orderService.createOrder(dto);
        return ResponseEntity.created(URI.create("/api/orders/" + responseDTO.getId()))
                .body(responseDTO);
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponseDTO> cancelOrder(@PathVariable Long orderId) {
        OrderResponseDTO canceled = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(canceled);
    }

    @PostMapping("/{orderId}/pay")
    public OrderResponseDTO payOrder(@PathVariable Long orderId) {
        return orderService.payOrder(orderId);
    }



}