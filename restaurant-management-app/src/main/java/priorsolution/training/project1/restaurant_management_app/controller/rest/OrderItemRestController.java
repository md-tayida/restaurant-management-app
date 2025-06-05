package priorsolution.training.project1.restaurant_management_app.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
//import priorsolution.training.project1.restaurant_management_app.dto.KitchenItemDTO;
import priorsolution.training.project1.restaurant_management_app.dto.OrderItemStatusUpdateDTO;
//import priorsolution.training.project1.restaurant_management_app.service.KitchenService;
import priorsolution.training.project1.restaurant_management_app.service.OrderItemService;
//import priorsolution.training.project1.restaurant_management_app.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orderItems-status")
@RequiredArgsConstructor
public class OrderItemRestController {

    private final OrderItemService orderItemService;


    @GetMapping("/preparing")
    public List<OrderItemStatusUpdateDTO> getAllPreparingItems() {
        return orderItemService.getAllPreparingItems();
    }

    /**
     * Get all items that are currently being prepared for a specific category
     */
    @GetMapping("/preparing/{category}")
    public List<OrderItemStatusUpdateDTO> getAllPreparingItemsByCategory(@PathVariable String category) {
        return orderItemService.getAllPreparingItems(category);
    }


    @PatchMapping("/preparing/{category}/{id}")
    public void markItemReadyToServe(@PathVariable String category, @PathVariable Long id) {
        // ใช้ category ตามต้องการ
        orderItemService.markItemReadyToServe(id);
    }

    @GetMapping("/ready-to-serve")
    public List<OrderItemStatusUpdateDTO> getReadyToServeItems(Authentication authentication) {

        return orderItemService.getReadyToServeItems();
    }
    @PatchMapping("/ready-to-serve/{id}")
    public void markItemDone(@PathVariable Long id) {
        orderItemService.markItemDone(id);
    }
    @GetMapping("/done")
    public List<OrderItemStatusUpdateDTO> getDoneItems(Authentication authentication) {

        return orderItemService.getReadyToServeItems();
    }

    @PutMapping("/{itemId}/cancel")
    public ResponseEntity<String> cancelOrderItem(@PathVariable Long itemId) {
        orderItemService.cancelOrderItem(itemId);
        return ResponseEntity.ok("Order item canceled successfully");
    }

}
