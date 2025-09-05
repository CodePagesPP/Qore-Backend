package com.example.Qore.service.Impl;

import com.example.Qore.repository.ClientRepository;
import com.example.Qore.repository.PlanRepository;
import com.example.Qore.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final ClientRepository clientRepo;
    private final PlanRepository planRepo;

    @Value("${mp.notification-url}")
    private String notificationUrl;

    @Override
    public String createCheckoutPreference(Long clientId, Long planId) {
        try {
            var client = clientRepo.findById(clientId).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            var plan   = planRepo.findById(planId).orElseThrow(() -> new RuntimeException("Plan no encontrado"));

            var item = com.mercadopago.client.preference.PreferenceItemRequest.builder()
                    .title(plan.getName())
                    .description(plan.getDescription())
                    .quantity(1)
                    .currencyId("PEN")
                    .unitPrice(BigDecimal.valueOf(plan.getPrice()))
                    .build();

            String externalRef = "type:one_time|client:" + client.getId() + "|plan:" + plan.getId();

            var backUrls = com.mercadopago.client.preference.PreferenceBackUrlsRequest.builder()
                    .success("http://localhost:4200/payment-success")
                    .pending("http://localhost:4200/payment-pending")
                    .failure("http://localhost:4200/payment-failure")
                    .build();

            var prefReq = com.mercadopago.client.preference.PreferenceRequest.builder()
                    .items(List.of(item))
                    .payer(com.mercadopago.client.preference.PreferencePayerRequest.builder()
                            .email(client.getEmail())
                            .name(client.getName())
                            .surname(client.getLastName())
                            .build())
                    .externalReference(externalRef)
                    .notificationUrl(notificationUrl)
                    .backUrls(backUrls)
                    //.autoReturn("approved")
                    .build();

            var prefClient = new com.mercadopago.client.preference.PreferenceClient();
            var preference = prefClient.create(prefReq);

            return preference.getInitPoint();

        } catch (com.mercadopago.exceptions.MPApiException e) {
            System.err.println("Error de MPApiException: " + e.getApiResponse().getContent());
            throw new RuntimeException("Error al crear preferencia en Mercado Pago", e);
        } catch (com.mercadopago.exceptions.MPException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear preferencia en Mercado Pago", e);
        }
    }

}

