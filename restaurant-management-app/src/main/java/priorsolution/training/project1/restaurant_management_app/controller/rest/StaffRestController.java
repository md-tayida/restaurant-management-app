package priorsolution.training.project1.restaurant_management_app.controller.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
//import priorsolution.training.project1.restaurant_management_app.dto.KitchenItemDTO;
import priorsolution.training.project1.restaurant_management_app.dto.OrderItemStatusUpdateDTO;
//import priorsolution.training.project1.restaurant_management_app.service.KitchenService;
import priorsolution.training.project1.restaurant_management_app.service.OrderItemService;
import priorsolution.training.project1.restaurant_management_app.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffRestController {

    private final OrderItemService orderItemService;
    private final UserService userService;

    public StaffRestController( OrderItemService orderItemService, UserService userService) {

        this.orderItemService = orderItemService;
        this.userService = userService;
    }

    @GetMapping("/hello")
    public ResponseEntity<String> helloStaff() {
        return ResponseEntity.ok("Hello STAFF, you are authenticated!");
    }
    @GetMapping("/ready-to-serve")
    public List<OrderItemStatusUpdateDTO> getReadyToServeItems(Authentication authentication) {

        return orderItemService.getReadyToServeItems();
    }
}