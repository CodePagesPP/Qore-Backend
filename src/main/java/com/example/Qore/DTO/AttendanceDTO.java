package com.example.Qore.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AttendanceDTO {
    private Long clientId;
    private String name;
    private String email;
    private String status;
}

