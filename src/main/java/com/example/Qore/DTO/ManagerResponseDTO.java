package com.example.Qore.DTO;

import com.example.Qore.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagerResponseDTO {
    private long id;
    private String email;
    private Role role;
    private String name;
    private String lastName;
    private String phoneNumber;
    private LocalDate birthday;
    private String sex;
    private String country;
    private String address;
    private String dni;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
