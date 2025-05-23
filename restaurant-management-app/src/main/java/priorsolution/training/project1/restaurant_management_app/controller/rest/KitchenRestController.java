//package priorsolution.training.project1.restaurant_management_app.controller.rest;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
////import priorsolution.training.project1.restaurant_management_app.dto.KitchenItemDTO;
//import priorsolution.training.project1.restaurant_management_app.dto.OrderItemStatusUpdateDTO;
//import priorsolution.training.project1.restaurant_management_app.service.KitchenService;
//import priorsolution.training.project1.restaurant_management_app.service.OrderItemService;
////import priorsolution.training.project1.restaurant_management_app.service.OrderService;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/kitchen")
//@RequiredArgsConstructor
//public class KitchenRestController {
//
//    private final KitchenService kitchenService;
//    private final OrderItemService orderItemService;
//
//    @GetMapping("/preparing/{category}")
//    public List<OrderItemStatusUpdateDTO> getKitchenItemsByCategory(@PathVariable String category) {
//        return kitchenService.getKitchenItemsByCategory(category);
//    }
//
//
//
//    @PutMapping("/item/{id}/ready-to-serve")
//    public void markItemReadyToServe(@PathVariable Long id) {
//        orderItemService.markItemReadyToServe(id);
//    }
//
//    @PutMapping("/item/{id}/done")
//    public void markItemDone(@PathVariable Long id) {
//        orderItemService.markItemCompleted(id);
//    }
//}