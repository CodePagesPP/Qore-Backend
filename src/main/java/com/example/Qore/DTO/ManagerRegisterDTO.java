package com.example.Qore.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ManagerRegisterDTO {
    private String email;
    private String password;
    private String name;
    private String lastName;
    private String phoneNumber;
    private LocalDate birthday;
    private String sex;
    private String country;
    private String address;
    private String dni;
}
