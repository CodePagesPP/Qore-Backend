package com.example.Qore.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ClientEndingSoon {
    private Long id;
    private String name;
    private String lastName;
    private String phoneNumber;
    private String email;
    private LocalDate subscriptionEnd;
    private long classesTaken;
    private long classesRemaining;
    private long totalClasses;
}
