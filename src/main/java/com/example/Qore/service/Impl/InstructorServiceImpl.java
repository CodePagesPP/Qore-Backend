package com.example.Qore.service.Impl;

import com.example.Qore.DTO.InstructorRegisterDTO;
import com.example.Qore.DTO.InstructorResponseDTO;
import com.example.Qore.DTO.InstructorUpdateDTO;
import com.example.Qore.model.Discipline;
import com.example.Qore.model.Instructor;
import com.example.Qore.model.RoleE;
import com.example.Qore.repository.DisciplineRepository;
import com.example.Qore.repository.InstructorRepository;
import com.example.Qore.repository.RoleRepository;
import com.example.Qore.service.InstructorService;
import com.example.Qore.service.PlanService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstructorServiceImpl implements InstructorService {
    private final InstructorRepository instructorRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final DisciplineRepository disciplineRepository;
    private final PlanServiceImpl planService;

    @Override
    public InstructorResponseDTO registerInstructor(InstructorRegisterDTO instructor) {
        if(instructorRepository.findByEmail(instructor.getEmail()).isPresent()){
            throw new EntityExistsException("User with this email already exists");
        };

        RoleE instructorRole = roleRepository.findByName("INSTRUCTOR")
                .orElseThrow(() -> new RuntimeException("Instructor Role not found"));

        List <Discipline> disciplines = planService.validateDisciplines(instructor.getDisciplineId());

        Instructor instructorCreate = Instructor.builder()
                .name(instructor.getName())
                .lastName(instructor.getLastName())
                .email(instructor.getEmail())
                .password(passwordEncoder.encode(instructor.getPassword()))
                .sex(instructor.getSex())
                .phoneNumber(instructor.getPhoneNumber())
                .dni(instructor.getDni())
                .birthday(instructor.getBirthday())
                .country(instructor.getAddress())
                .city(instructor.getCity())
                .address(instructor.getAddress())
                .role(instructorRole)
                .discipline(disciplines)
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .build();

        return mapToDTO(instructorRepository.save(instructorCreate));
    }

    @Override
    public List<InstructorResponseDTO> getAllInstructors() {
        return instructorRepository.findInstructorByRoleName("INSTRUCTOR").stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InstructorResponseDTO updateInstructor(String dni, InstructorUpdateDTO instructor) {
        Instructor instructorFound = instructorRepository.findInstructorByDniAndRole(dni)
                .orElseThrow(() -> new EntityNotFoundException("Instructor not found or is not an Instructor"));

        if(instructor.getName() != null) instructorFound.setName(instructor.getName());
        if(instructor.getLastName() != null) instructorFound.setLastName(instructor.getLastName());
        if(instructor.getEmail() != null) instructorFound.setEmail(instructor.getEmail());
        if(instructor.getPassword() != null) instructorFound.setPassword(passwordEncoder.encode(instructor.getPassword()));
        if(instructor.getSex() != null) instructorFound.setSex(instructor.getSex());
        if(instructor.getPhoneNumber()!= null) instructorFound.setPhoneNumber(instructor.getPhoneNumber());
        if(instructor.getDni() != null) instructorFound.setDni(instructor.getDni());
        if(instructor.getBirthday() != null) instructorFound.setBirthday(instructor.getBirthday());
        if(instructor.getCountry() != null) instructorFound.setAddress(instructor.getAddress());
        if(instructor.getCity() != null) instructorFound.setCity(instructor.getCity());
        if(instructor.getAddress() != null) instructorFound.setAddress(instructor.getAddress());
        if (instructor.getDisciplineId() != null) instructorFound.setDiscipline(planService.validateDisciplines(instructor.getDisciplineId()));
        instructorFound.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        return mapToDTO(instructorRepository.save(instructorFound));
    }

    @Override
    public void deleteInstructor(String dni) {
        Instructor instructorFound = instructorRepository.findInstructorByDni(dni)
                .orElseThrow(() -> new EntityNotFoundException("Instructor not found or is not an Instructor"));
        instructorRepository.delete(instructorFound);
    }

    private InstructorResponseDTO mapToDTO(Instructor instructor){

        List<Long> disciplines = instructor.getDiscipline().stream().map(Discipline::getId).collect(Collectors.toList());

        return InstructorResponseDTO.builder()
                .dni(instructor.getDni())
                .email(instructor.getEmail())
                .role(instructor.getRole())
                .id(instructor.getId())
                .name(instructor.getName())
                .sex(instructor.getSex())
                .lastName(instructor.getLastName())
                .address(instructor.getAddress())
                .phoneNumber(instructor.getPhoneNumber())
                .birthday(instructor.getBirthday())
                .country(instructor.getAddress())
                .city(instructor.getCity())
                .disciplineId(disciplines)
                .createdAt(instructor.getCreatedAt())
                .updatedAt(instructor.getUpdatedAt())
                .build();
    }
}
