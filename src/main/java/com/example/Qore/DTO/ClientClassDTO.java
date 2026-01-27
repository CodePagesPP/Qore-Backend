package com.example.Qore.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientClassDTO {
    private Long id;
    private String name;
    private String instructorName;
    private String room;
    private LocalDate startDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String disciplineName;
    private String status;
    private String attendance;
}
