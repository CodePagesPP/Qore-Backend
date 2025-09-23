package com.example.Qore.service.Impl;

import com.example.Qore.DTO.InstructorRegisterDTO;
import com.example.Qore.DTO.InstructorResponseDTO;
import com.example.Qore.DTO.InstructorStatsDTO;
import com.example.Qore.DTO.InstructorUpdateDTO;
import com.example.Qore.model.ClassSession;
import com.example.Qore.model.Discipline;
import com.example.Qore.model.Enum.EstadoSession;
import com.example.Qore.model.Instructor;
import com.example.Qore.model.RoleE;
import com.example.Qore.repository.ClassSessionRepository;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstructorServiceImpl implements InstructorService {
    private final InstructorRepository instructorRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final ClassSessionRepository classSessionRepository;
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

    @Override
    public InstructorResponseDTO getInstructorByDni(String dni) {
        Instructor instructor = instructorRepository.findInstructorByDni(dni)
                .orElseThrow(() -> new EntityNotFoundException("Instructor not found or is not an Instructor"));
        return mapToDTO(instructor);
    }

    public InstructorStatsDTO getInstructorStats(Long instructorId, Integer month, Integer year) {
        Instructor instructor = (Instructor) instructorRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor no encontrado"));

        // Si no pasan mes/año, usar actual
        LocalDate now = LocalDate.now();
        int m = (month != null) ? month : now.getMonthValue();
        int y = (year != null) ? year : now.getYear();

        LocalDate start = LocalDate.of(y, m, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        // Clases del mes
        List<ClassSession> sessions = classSessionRepository
                .findByInstructorIdAndStartDateBetween(instructorId, start, end);

        int totalClasses = sessions.size();

        //  Contar clases pendientes
        long pendingClasses = classSessionRepository.countByInstructorIdAndStartDateBetweenAndEstado(
                instructorId, start, end, EstadoSession.PENDIENTE);


        // Contar alumnos únicos en todas las clases del mes
        Set<Long> studentIds = new HashSet<>();
        for (ClassSession s : sessions) {
            s.getClients().forEach(c -> studentIds.add(c.getId()));
        }
        int totalStudents = studentIds.size();

        return InstructorStatsDTO.builder()
                .id(instructor.getId())
                .name(instructor.getName())
                .lastName(instructor.getLastName())
                .email(instructor.getEmail())
                .dni(instructor.getDni())
                .phoneNumber(instructor.getPhoneNumber())
                .totalClassesThisMonth(totalClasses)
                .totalStudentsThisMonth(totalStudents)
                .pendingClassesThisMonth((int) pendingClasses) // nuevo campo
                .build();
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
