package com.example.Qore.DTO;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassSessionDTO {
    private Long id;
    private String name;
    private Long disciplineId;
    private Long instructorId;
    private Long roomId;
    private int capacity;
    private String estado;
    private LocalDate startDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean repeat;
    private LocalDate repeatUntil; // hasta qué fecha repetir

    private String comentario;
    private Set<DayOfWeek> repeatDays;// qué día repetir (MONDAY, TUESDAY...)

    private Integer repeatInterval;
    private Set<Long> clientIds;
    private int currentCount;
    private boolean joined;
}
