package com.example.Qore.controller;

import com.example.Qore.service.Impl.PaymentProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payments-webhook")
@RequiredArgsConstructor
public class PaymentWebhookController {

    private final PaymentProcessor paymentProcessor;

    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestParam Map<String, String> query,
                                          @RequestBody(required = false) Map<String, Object> body) {
        try {
            String type = query.getOrDefault("type", body != null ? String.valueOf(body.get("type")) : null);

            String dataId = query.get("data.id");
            if (dataId == null && body != null && body.get("data") instanceof Map<?,?> data) {
                Object idObj = data.get("id");
                if (idObj != null) dataId = String.valueOf(idObj);
            }
            if ("payment".equalsIgnoreCase(type) && dataId != null) {
                paymentProcessor.processPayment(Long.parseLong(dataId));
            }
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("error: " + e.getMessage());
        }
    }
}
