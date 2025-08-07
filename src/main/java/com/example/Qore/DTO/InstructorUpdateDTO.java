package com.example.Qore.DTO;

import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Data
public class InstructorUpdateDTO {
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
    private List<Long> disciplineId;
    private Timestamp updatedAt;
}
