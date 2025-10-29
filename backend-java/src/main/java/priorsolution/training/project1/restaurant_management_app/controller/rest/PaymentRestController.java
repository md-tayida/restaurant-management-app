package priorsolution.training.project1.restaurant_management_app.controller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import priorsolution.training.project1.restaurant_management_app.Response.PaymentResponse;
import priorsolution.training.project1.restaurant_management_app.Response.PaymentsByStatusResponse;
import priorsolution.training.project1.restaurant_management_app.entity.enums.PaymentStatusEnum;
import priorsolution.training.project1.restaurant_management_app.request.PaymentRequest;
import priorsolution.training.project1.restaurant_management_app.request.PaymentStatusRequest;
import priorsolution.training.project1.restaurant_management_app.service.PaymentService;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentRestController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPaymentByOrderId(@Valid @RequestBody PaymentRequest request) {
        log.info("API POST /api/payments called with orderId: {}", request.getOrderId());
        PaymentResponse response = paymentService.createPaymentByOrderId(request);
        return ResponseEntity.status(201).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PaymentResponse> updatePayment(
            @PathVariable("id") Long id,
            @Valid @RequestBody PaymentRequest request
    ) {
        log.info("API PATCH /api/payments/{} called", id);
        PaymentResponse response = paymentService.updatePayment(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PaymentsByStatusResponse>> getPaymentsByStatus(
            @RequestParam PaymentStatusEnum status) {

        List<PaymentsByStatusResponse> responses = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(responses);
    }


}
