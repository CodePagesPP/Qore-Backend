package com.example.Qore.model.payment;

import com.example.Qore.model.Client;
import com.example.Qore.model.Plan;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="payments")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;


    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;


    @Column(nullable = false, unique = true)
    private String mpPaymentId;


    @Column(nullable = false)
    private String status;


    @Column(nullable = false)
    private Float amount;


    private LocalDateTime paymentDate;


    private String subscriptionId;
}
