package priorsolution.training.project1.restaurant_management_app.controller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import priorsolution.training.project1.restaurant_management_app.Response.OrderResponse;
import priorsolution.training.project1.restaurant_management_app.Response.TableInfoResponse;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderStatusEnum;
import priorsolution.training.project1.restaurant_management_app.request.OrderRequest;
import priorsolution.training.project1.restaurant_management_app.request.OrderStatusRequest;
import priorsolution.training.project1.restaurant_management_app.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderRestController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        log.info("API GET /api/orders called");
        List<OrderResponse> response = orderService.getAllOrders();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        log.info("API GET /api/orders/{} called", id);
        OrderResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        log.info("API POST /api/orders called with body: {}", request);
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(201).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Long id,
                                                           @Valid @RequestBody OrderStatusRequest request) {
        log.info("API PATCH /api/orders/{} called with body: {}", id, request);
        OrderResponse updated = orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(updated);
    }
    @GetMapping("/table/{tableId}")
    public ResponseEntity<OrderResponse> getActiveOrderByTableId(@PathVariable Long tableId) {
        log.info("API GET /api/orders/table/{} called", tableId);
        OrderResponse response = orderService.getActiveOrderByTableId(tableId);
        return ResponseEntity.ok(response);
    }

}
