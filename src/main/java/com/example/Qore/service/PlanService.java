package com.example.Qore.service;
import com.example.Qore.DTO.PlanRegisterDTO;
import com.example.Qore.DTO.PlanResponseDTO;
import com.example.Qore.DTO.PlanUpdateDTO;
import com.example.Qore.model.Plan;

import java.util.List;

public interface PlanService {
    PlanResponseDTO create(PlanRegisterDTO planDTO);
    PlanResponseDTO update(Long id, PlanUpdateDTO plan);
    void delete(Long id);
    List<PlanResponseDTO> getAllPlans();
}
