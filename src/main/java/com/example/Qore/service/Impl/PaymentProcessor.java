package com.example.Qore.service.Impl;

import com.example.Qore.model.Client;
import com.example.Qore.model.Plan;
import com.example.Qore.model.payment.Payment;
import com.example.Qore.repository.ClientRepository;
import com.example.Qore.repository.PaymentRepository;
import com.example.Qore.repository.PlanRepository;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.mercadopago.client.payment.PaymentClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentProcessor {

    private final PaymentRepository paymentRepo;
    private final ClientRepository clientRepo;
    private final PlanRepository planRepo;

    public void processPayment(Long mpPaymentId) {
        try {
            PaymentClient paymentClient = new PaymentClient();
            com.mercadopago.resources.payment.Payment mpPayment = paymentClient.get(mpPaymentId); // throws MPException, MPApiException

            // Sólo nos interesa estado aprobado
            if (mpPayment == null || !"approved".equalsIgnoreCase(mpPayment.getStatus())) return;

            //si ya lo tenemos, no repetimos
            if (paymentRepo.findByMpPaymentId(String.valueOf(mpPaymentId)).isPresent()) return;

            // Recuperar datos desde external_reference
            String externalRef = mpPayment.getExternalReference();
            if (externalRef == null || externalRef.isBlank()) {
                throw new IllegalStateException("El pago no contiene external_reference, imposible conciliar");
            }

            // Extraer clientId y planId
            Long clientId = parseLong(externalRef, "client");
            Long planId   = parseLong(externalRef, "plan");

            var client = clientRepo.findById(clientId).orElseThrow(() -> new IllegalStateException("Cliente no encontrado"));
            var plan   = planRepo.findById(planId).orElseThrow(() -> new IllegalStateException("Plan no encontrado"));

            // transaction amount (puede venir como BigDecimal o Double según versión)
            var amount = mpPayment.getTransactionAmount();
            BigDecimal bdAmount = amount instanceof BigDecimal ? (BigDecimal) amount : BigDecimal.valueOf(((Number) amount).doubleValue());

            persistPaymentAndActivate(client, plan, mpPaymentId, bdAmount);

        } catch (MPApiException | MPException e) {
            // loguea y re-lanza o maneja
            throw new RuntimeException("Error al obtener pago desde Mercado Pago: " + e.getMessage(), e);
        }
    }

    private void persistPaymentAndActivate(Client client, Plan plan, Long mpPaymentId, BigDecimal amount) {
        var p = Payment.builder()
                .client(client)
                .plan(plan)
                .mpPaymentId(String.valueOf(mpPaymentId))
                .status("approved")
                .amount(amount.floatValue())
                .paymentDate(LocalDateTime.now())
                .build();
        paymentRepo.save(p);

        // asignar plan pero no activar vigencia todavía
        client.setPlan(plan);
        client.setSubscriptionStart(null);
        client.setSubscriptionEnd(null);
        clientRepo.save(client);
    }

    private Long parseLong(String externalRef, String key) {
        for (String part : externalRef.split("\\|")) {
            if (part.startsWith(key + ":")) return Long.valueOf(part.substring((key + ":").length()));
        }
        throw new IllegalStateException("No se encontró '" + key + "' en external_reference");
    }
}
