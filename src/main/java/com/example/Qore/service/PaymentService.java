package com.example.Qore.service;

import com.example.Qore.DTO.PaymentDTO;

import java.time.YearMonth;
import java.util.Map;

public interface PaymentService {
    PaymentDTO simulatePayment(Long clientId, Long planId);
    Map<YearMonth, Double> getMonthlyIncomes();
    Map<String, Double> getWeeklyIncomes();
}
