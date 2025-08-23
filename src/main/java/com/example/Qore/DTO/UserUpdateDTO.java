package com.example.Qore.DTO;

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
public class UserUpdateDTO {
    private String email;
    private String password;
    private long roleId;
    private String name;
    private String lastName;
    private String phoneNumber;
    private LocalDate birthday;
    private String sex;
    private String country;
    private String city;
    private String address;
    private String dni;
    private Timestamp updatedAt;
}
