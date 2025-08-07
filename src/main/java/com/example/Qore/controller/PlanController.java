package com.example.Qore.controller;

import com.example.Qore.DTO.PlanRegisterDTO;
import com.example.Qore.DTO.PlanResponseDTO;
import com.example.Qore.DTO.PlanUpdateDTO;
import com.example.Qore.model.Plan;
import com.example.Qore.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/plans")
@RequiredArgsConstructor
public class PlanController {
    private final PlanService planService;

    @PostMapping
    public ResponseEntity<PlanResponseDTO> addPlan(@RequestBody PlanRegisterDTO plan) {
        return ResponseEntity.ok(planService.create(plan));
    }

    @PutMapping("/updatePlan/{id}")
    public ResponseEntity<PlanResponseDTO> updatePlan(@PathVariable("id") Long id, @RequestBody PlanUpdateDTO plan) {
        return ResponseEntity.ok(planService.update(id, plan));
    }

    @GetMapping("/listPlans")
    public ResponseEntity<List<PlanResponseDTO>> getAll() {
        return ResponseEntity.ok(planService.getAllPlans());
    }

    @DeleteMapping("/deletePlan/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable("id") Long id) {
        planService.delete(id);
        return ResponseEntity.ok().build();
    }
}
