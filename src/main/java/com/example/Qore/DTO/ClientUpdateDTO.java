package com.example.Qore.DTO;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ClientUpdateDTO {
    private String email;
    private String password;
    private String name;
    private String lastName;
    private String phoneNumber;
    private LocalDate birthday;
    private String sex;
    private String country;
    private String city;
    private String address;
    private String dni;
    private List<Long> disciplineIds;
}
