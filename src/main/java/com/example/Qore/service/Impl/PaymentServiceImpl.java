package com.example.Qore.service.Impl;

import com.example.Qore.DTO.PaymentDTO;
import com.example.Qore.model.Client;
import com.example.Qore.model.Plan;
import com.example.Qore.model.payment.Payment;
import com.example.Qore.repository.ClientRepository;
import com.example.Qore.repository.PaymentRepository;
import com.example.Qore.repository.PlanRepository;
import com.example.Qore.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final ClientRepository clientRepository;
    private final PlanRepository planRepository;

    @Override
    public PaymentDTO simulatePayment(Long clientId, Long planId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

        if (client.getPlan() != null && client.getSubscriptionEnd() != null) {
            if (client.getSubscriptionEnd().isAfter(LocalDate.now())) {
                throw new RuntimeException(
                        "El cliente ya tiene un plan activo hasta " + client.getSubscriptionEnd()
                );
            }
        }

        Payment payment = Payment.builder()
                .client(client)
                .plan(plan)
                .mpPaymentId("TEST-" + System.currentTimeMillis())
                .status("approved")
                .amount(plan.getPrice())
                .paymentDate(LocalDateTime.now())
                .subscriptionId("subscription".equalsIgnoreCase(plan.getSellType())
                        ? "SUBS-" + clientId
                        : null)
                .build();

        paymentRepository.save(payment);
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(plan.getDuration());

        client.setPlan(plan);
        client.setSubscriptionStart(start);
        client.setSubscriptionEnd(end);
        clientRepository.save(client);


        return PaymentDTO.builder()
                .id(payment.getId())
                .clientName(client.getName() + " " + client.getLastName())
                .clientEmail(client.getEmail())
                .planName(plan.getName())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paymentDate(payment.getPaymentDate())
                .subscriptionId(payment.getSubscriptionId())
                .build();
    }

    @Override
    public Map<YearMonth, Double> getMonthlyIncomes() {
        return paymentRepository.findAllByStatus("approved").stream()
                .collect(Collectors.groupingBy(
                        p -> YearMonth.from(p.getPaymentDate()),
                        Collectors.summingDouble(Payment::getAmount)
                ));
    }

    public Map<String, Double> getWeeklyIncomes() {
        List<Payment> payments = paymentRepository.findAll();
        Map<String, Double> result = new HashMap<>();

        WeekFields weekFields = WeekFields.ISO;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

        for (Payment p : payments) {
            LocalDate date = p.getPaymentDate().toLocalDate();
            int week = date.get(weekFields.weekOfWeekBasedYear());
            int year = date.getYear();

            // Calcular inicio y fin de la semana
            LocalDate startOfWeek = date.with(weekFields.dayOfWeek(), 1); // Lunes
            LocalDate endOfWeek = date.with(weekFields.dayOfWeek(), 7);   // Domingo

            String key = year + " | "
                    + startOfWeek.format(formatter) + " - "
                    + endOfWeek.format(formatter);

            result.merge(key, Double.valueOf(p.getAmount()), Double::sum);
        }
        return result;
    }


    public Double getCurrentMonthIncome() {
        YearMonth currentMonth = YearMonth.now();

        return paymentRepository.findAllByStatus("approved").stream()
                .filter(p -> YearMonth.from(p.getPaymentDate()).equals(currentMonth))
                .collect(Collectors.summingDouble(Payment::getAmount));
    }
}
