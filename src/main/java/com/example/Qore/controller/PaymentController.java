package com.example.Qore.controller;

import com.example.Qore.DTO.PaymentDTO;
import com.example.Qore.model.payment.Payment;
import com.example.Qore.service.CheckoutService;
import com.example.Qore.service.Impl.PaymentProcessor;
import com.example.Qore.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final CheckoutService checkoutService;
    private final PaymentService paymentService;
    private final PaymentProcessor paymentProcessor;
    @PostMapping("/test")
    public ResponseEntity<PaymentDTO> simulatePayment(
            @RequestParam Long clientId,
            @RequestParam Long planId
    ) {
        PaymentDTO payment = paymentService.simulatePayment(clientId, planId);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/checkout")
    public ResponseEntity<Map<String,String>> startCheckout(
            @RequestParam Long clientId, @RequestParam Long planId) {
        String initPoint = checkoutService.createCheckoutPreference(clientId, planId);
        return ResponseEntity.ok(Map.of("init_point", initPoint));
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmPayment(@RequestBody Map<String, Object> body) {
        Long paymentId = Long.valueOf(body.get("paymentId").toString());
        paymentProcessor.processPayment(paymentId);
        return ResponseEntity.ok("confirmed");
    }
}
