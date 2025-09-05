package com.example.Qore.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDTO {
    private Long id;
    private String clientName;
    private String clientEmail;
    private String planName;
    private Float amount;
    private String status;
    private LocalDateTime paymentDate;
    private String subscriptionId;
}

