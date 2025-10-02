package com.example.Qore.DTO;

import com.example.Qore.model.Enum.EstadoSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassSessionUpdateDTO {
    private Long id;
    private String name;
    private Long disciplineId;
    private Long instructorId;
    private Long roomId;
    private int capacity;
    private LocalDate startDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private EstadoSession Estado;
    private boolean repeat;
}

