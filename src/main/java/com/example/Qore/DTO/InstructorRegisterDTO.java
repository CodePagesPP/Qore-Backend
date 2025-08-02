package com.example.Qore.DTO;

import com.example.Qore.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstructorRegisterDTO {
    private String name;
    private String lastName;
    private String email;
    private String password;
    private String sex;
    private String phoneNumber;
    private String dni;
    private LocalDate birthday;
    private String country;
    private String city;
    private String address;
    private String discipline;
}
