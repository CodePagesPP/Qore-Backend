package com.example.Qore.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name="clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Client extends User{

    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;
    private LocalDate subscriptionStart;
    private LocalDate subscriptionEnd;
}


