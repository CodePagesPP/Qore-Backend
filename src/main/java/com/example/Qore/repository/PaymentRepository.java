package com.example.Qore.repository;

import com.example.Qore.model.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByStatus(String status);
    Optional<Payment> findByMpPaymentId(String mpPaymentId);
}