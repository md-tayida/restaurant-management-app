package priorsolution.training.project1.restaurant_management_app.controller.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import priorsolution.training.project1.restaurant_management_app.Response.KitchenManagementResponse;
import priorsolution.training.project1.restaurant_management_app.Response.OrderItemResponse;
import priorsolution.training.project1.restaurant_management_app.Response.OrderResponse;
import priorsolution.training.project1.restaurant_management_app.service.KitchenManagementService;

import java.util.List;

@RestController
@RequestMapping("/api/kitchen-management")
@RequiredArgsConstructor
@Slf4j
public class KitchenManagementRestController {
private final KitchenManagementService kitchenManagementService;


    @GetMapping
    public ResponseEntity<List<KitchenManagementResponse>> getAllActiveOrders() {
        log.info("API GET /api/kitchen-management called");
        List<KitchenManagementResponse> response = kitchenManagementService.getAllActiveOrders();
        return ResponseEntity.ok(response);
    }
}
