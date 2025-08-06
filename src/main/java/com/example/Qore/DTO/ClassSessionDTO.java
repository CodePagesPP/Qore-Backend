package com.example.Qore.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private LocalDate startDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean repeat;
    private Set<Long> clientIds;
}
