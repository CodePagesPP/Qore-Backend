package com.example.Qore.DTO;

import com.example.Qore.model.RoleE;
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

public class InstructorResponseDTO {
    private long id;
    private String email;
    private RoleE role;
    private String name;
    private String lastName;
    private String phoneNumber;
    private LocalDate birthday;
    private String sex;
    private String country;
    private String city;
    private String address;
    private String dni;
    private String discipline;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
