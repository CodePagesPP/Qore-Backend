package com.example.Qore.service.Impl;

import com.example.Qore.DTO.PlanRegisterDTO;
import com.example.Qore.DTO.PlanResponseDTO;
import com.example.Qore.DTO.PlanUpdateDTO;
import com.example.Qore.model.Discipline;
import com.example.Qore.model.Plan;
import com.example.Qore.repository.DisciplineRepository;
import com.example.Qore.repository.PlanRepository;
import com.example.Qore.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {
    private final PlanRepository planRepository;
    private final DisciplineRepository disciplineRepository;

    public List<Discipline> validateDisciplines(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalStateException("Insert at least one discipline");
        }
        List<Discipline> found = disciplineRepository.findAllById(ids);
        if (found.size() != ids.size()) {
            throw new IllegalStateException("One or more IDs does not exist");
        }
        return found;
    }

    @Override
    public PlanResponseDTO create(PlanRegisterDTO plan) {
        if (planRepository.findByName(plan.getName()).isPresent()) {
            throw new IllegalArgumentException("Existing plan");
        }

        List <Discipline> disciplines = validateDisciplines(plan.getDiscipline_id());

        Plan planCreate = Plan.builder()
                .name(plan.getName())
                .disciplines(disciplines)
                .description(plan.getDescription())
                .sessions(plan.getSessions())
                .payMethod(plan.getPayMethod())
                .duration(plan.getDuration())
                .price(plan.getPrice())
                .sellType(plan.getSellType())
                .active(plan.isActive())
                .reprograms(plan.getReprograms())
                .build();

        return mapToDTO(planRepository.save(planCreate));
    }

    @Override
    public PlanResponseDTO update(Long id, PlanUpdateDTO plan) {
        Plan planFound = planRepository.findById(id).orElseThrow(()  -> new IllegalArgumentException("Plan not found"));

        if(plan.getName() != null) planFound.setName(plan.getName());
        if(plan.getDiscipline_id() != null) planFound.setDisciplines(validateDisciplines(plan.getDiscipline_id()));
        if(plan.getDescription() != null) planFound.setDescription(plan.getDescription());
        if(plan.getSessions() != null) planFound.setSessions(plan.getSessions());
        if (plan.getPayMethod() != null) planFound.setPayMethod(plan.getPayMethod());
        if (plan.getDuration() != null) planFound.setDuration(plan.getDuration());
        if (plan.getPrice() != null) planFound.setPrice(plan.getPrice());
        if (plan.getSellType() != null) planFound.setSellType(plan.getSellType());
        if (plan.getActive() != null) planFound.setActive(plan.getActive());
        if (plan.getReprograms() != null) planFound.setReprograms(plan.getReprograms());

        return mapToDTO(planRepository.save(planFound));
    }

    @Override
    public void delete(Long id) {
        Plan planFound = planRepository.findById(id).orElseThrow(()  -> new IllegalArgumentException("Plan not found"));
        planRepository.delete(planFound);
    }

    @Override
    public List<PlanResponseDTO> getAllPlans() {
        return planRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private PlanResponseDTO mapToDTO(Plan plan) {

        List<Long> disciplines = plan.getDisciplines().stream().map(Discipline::getId).collect(Collectors.toList());

        return PlanResponseDTO.builder()
                .id(plan.getId())
                .name(plan.getName())
                .discipline_id(disciplines)
                .description(plan.getDescription())
                .sessions(plan.getSessions())
                .payMethod(plan.getPayMethod())
                .duration(plan.getDuration())
                .price(plan.getPrice())
                .sellType(plan.getSellType())
                .active(plan.getActive())
                .reprograms(plan.getReprograms())
                .build();
    }
}
