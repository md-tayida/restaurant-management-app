//package priorsolution.training.project1.restaurant_management_app.controller.rest;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import priorsolution.training.project1.restaurant_management_app.dto.PaymentRequestDTO;
//import priorsolution.training.project1.restaurant_management_app.service.PaymentService;
//
//@RestController
//@RequestMapping("/api/payments")
//@RequiredArgsConstructor
//public class PaymentRestController {
//
//    private final PaymentService paymentService;
//
//    @PostMapping
//    public ResponseEntity<String> payOrder(@RequestBody PaymentRequestDTO request) {
//        paymentService.payOrder(request);
//        return ResponseEntity.ok("ชำระเงินสำเร็จ");
//
//
//    }
//
//    @PostMapping("/by-table/{tableId}")
//    public ResponseEntity<String> payByTable(@PathVariable Long tableId) {
//        paymentService.payOrder(PaymentRequestDTO.builder().build());
//        return ResponseEntity.ok("ชำระเงินสำเร็จสำหรับโต๊ะ " + tableId);
//    }
//
//    @ExceptionHandler(IllegalStateException.class)
//    public ResponseEntity<String> handleIllegalState(IllegalStateException ex) {
//        return ResponseEntity.badRequest().body(ex.getMessage());
//    }
//}