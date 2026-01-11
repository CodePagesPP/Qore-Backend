package com.example.Qore.DTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientPlanHistoryDTO {
    private Long id;
    private String planName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Float pricePaid;
    private LocalDateTime assignedAt;
    private String assignedBy;
    private String paymentMethod;
}
