package com.example.Qore.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientPlanInfoDTO {
    private Long id;
    private String name;
    private String lastName;
    private String email;
    private String dni;
    private String phoneNumber;
    private String planName;
    private LocalDate subscriptionEnd;
    private long totalClasses;
    private long classesTaken;
    private long classesRemaining;
    private boolean trialCompleted;
}

