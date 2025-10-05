package com.example.Qore.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Client extends User{

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean trialCompleted = false;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "client_disciplines",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "discipline_id")
    )
    private List<Discipline> disciplines = new ArrayList<>();


    private LocalDate subscriptionStart;
    private LocalDate subscriptionEnd;
}


