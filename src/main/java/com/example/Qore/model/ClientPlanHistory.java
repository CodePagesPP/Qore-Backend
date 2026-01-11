package com.example.Qore.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="client_plan_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientPlanHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;


    private String planName;

    private LocalDate startDate;
    private LocalDate endDate;
    private Float pricePaid;

    private LocalDateTime assignedAt;
    private String assignedBy;
    private String paymentMethod;
}
