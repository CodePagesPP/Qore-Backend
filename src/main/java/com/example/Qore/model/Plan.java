package com.example.Qore.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name="Plans")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Discipline> disciplines;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer sessions;

    @Column(nullable = false)
    private String payMethod;

    @Column(nullable = false)
    private Integer duration;

    @Column(nullable = false)
    private Float price;

    @Column(nullable = false)
    private String sellType;

    @Column(nullable = false)
    private Boolean active=true;

    @Column(nullable = false)
    private Integer reprograms;
}
