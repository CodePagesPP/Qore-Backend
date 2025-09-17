package com.example.Qore.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientRegisterNewDTO {
    private Long id;
    private String name;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Timestamp createdAt;
}