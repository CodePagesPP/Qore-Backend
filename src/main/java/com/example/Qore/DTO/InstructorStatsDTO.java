package com.example.Qore.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InstructorStatsDTO {
    private Long id;
    private String name;
    private String lastName;
    private String email;
    private String dni;
    private String phoneNumber;

    private int totalClassesThisMonth;
    private int totalStudentsThisMonth;
    private int pendingClassesThisMonth;
}