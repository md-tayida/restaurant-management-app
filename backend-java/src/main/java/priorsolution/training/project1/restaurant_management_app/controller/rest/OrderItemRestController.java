package priorsolution.training.project1.restaurant_management_app.controller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import priorsolution.training.project1.restaurant_management_app.Response.OrderItemResponse;
import priorsolution.training.project1.restaurant_management_app.Response.OrderResponse;
import priorsolution.training.project1.restaurant_management_app.request.OrderItemStatusRequest;
import priorsolution.training.project1.restaurant_management_app.service.OrderItemService;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
@Slf4j
public class OrderItemRestController {

    private final OrderItemService orderItemService;

    @PatchMapping("/{id}/flow")
    public ResponseEntity<OrderItemResponse> updateOrderItemStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderItemStatusRequest request
    ) {
        log.info("API PATCH /api/order-items/{}/status called with body: {}", id, request);
        OrderItemResponse response = orderItemService.updateOrderItemStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<OrderItemResponse>> getAllOrderItems() {
        log.info("API GET /api/orders-items called");
        List<OrderItemResponse> response = orderItemService.getAllOrderItems();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItemResponse> getOrderItemById(@PathVariable Long id) {
        log.info("API GET /api/orders-items/{} called", id);
        OrderItemResponse response = orderItemService.getOrderItemById(id);
        return ResponseEntity.ok(response);
    }
}

