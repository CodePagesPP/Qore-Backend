package com.example.Qore.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="Instructors")
@Data
@NoArgsConstructor
@SuperBuilder
public class Instructor extends User{
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Discipline> discipline;
}
