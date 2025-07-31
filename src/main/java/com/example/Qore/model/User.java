package com.example.Qore.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(unique = false)
    private String name;

    @Column(unique = false)
    private String lastName;

    @Column(unique = false)
    private String sex;

    @Column(unique = true)
    private String phoneNumber;

    @Column(unique = true)
    private String dni;

    @Column(unique = false)
    private LocalDate birthday;

    @Column(unique = false)
    private String country;

    @Column(unique = false)
    private String address;

    @Column(unique = false)
    private Timestamp createdAt;

    @Column(unique = false)
    private Timestamp updatedAt;
}

