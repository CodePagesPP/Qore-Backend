package com.example.Qore.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanResponseDTO {
    private Long id;
    private String name;
    private List<Long> discipline_id;
    private String description;
    private int sessions;
    private String payMethod;
    private int duration;
    private float price;
    private String sellType;
    private boolean active=true;
    private int reprograms;
}
