package com.example.Qore.controller;

import com.example.Qore.DTO.PaymentDTO;
import com.example.Qore.model.payment.Payment;
import com.example.Qore.service.CheckoutService;
import com.example.Qore.service.Impl.PaymentProcessor;
import com.example.Qore.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.HashMap;
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


    @GetMapping("/current-month-income")
    public ResponseEntity<Map<String, Object>> getCurrentMonthIncome() {
        Double total = paymentService.getCurrentMonthIncome();
        double goal = 5000.0;
        double percentage = (total / goal) * 100;

        Map<String, Object> response = new HashMap<>();
        response.put("month", YearMonth.now().toString());
        response.put("total", total);
        response.put("goal", goal);
        response.put("percentage", percentage);

        return ResponseEntity.ok(response);
    }
}
