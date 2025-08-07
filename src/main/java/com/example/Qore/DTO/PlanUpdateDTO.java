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
public class PlanUpdateDTO {
    private String name;
    private List<Long> discipline_id;
    private String description;
    private Integer sessions;
    private String payMethod;
    private Integer duration;
    private Float price;
    private String sellType;
    private Boolean active=true;
    private Integer reprograms;
}
