package com.example.Qore.service.Impl;

import com.example.Qore.DTO.InstructorRegisterDTO;
import com.example.Qore.DTO.InstructorResponseDTO;
import com.example.Qore.DTO.InstructorStatsDTO;
import com.example.Qore.DTO.InstructorUpdateDTO;
import com.example.Qore.model.*;
import com.example.Qore.model.Enum.EstadoSession;
import com.example.Qore.repository.*;
import com.example.Qore.service.InstructorService;
import com.example.Qore.service.PlanService;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    private final JavaMailSender mailSender;
    private final ManagerRepository managerRepository;
    private final StaffRepository staffRepository;

    @Override
    public InstructorResponseDTO registerInstructor(InstructorRegisterDTO instructor) {
        if(instructorRepository.findByEmail(instructor.getEmail()).isPresent()){
            throw new EntityExistsException("User with this email already exists");
        };

        RoleE instructorRole = roleRepository.findByName("INSTRUCTOR")
                .orElseThrow(() -> new RuntimeException("Instructor Role not found"));



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

    @Override
    public void sendComentarioNotificationEmail(ClassSession session) {
        List<String> recipients = new ArrayList<>();

        // Obtenemos todos los managers y staff
        List<Manager> managers = managerRepository.findAll();
        List<Staff> staffList = staffRepository.findAll();

        // Extraemos los correos
        managers.forEach(m -> recipients.add(m.getEmail()));
        staffList.forEach(s -> recipients.add(s.getEmail()));

        if (recipients.isEmpty()) return;

        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            DateTimeFormatter commentFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (String email : recipients) {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setTo(email);
                helper.setSubject("Nuevo comentario del instructor en una clase");

                // 🕒 Formateamos la fecha y hora de la clase
                String fechaClase = session.getStartDate() != null
                        ? session.getStartDate().format(dateFormatter)
                        : "N/A";


                String horaInicio = session.getStartTime() != null ? session.getStartTime().toString() : "N/A";
                String horaFin = session.getEndTime() != null ? session.getEndTime().toString() : "N/A";

                // 📄 Contenido HTML del correo
                String content = """
            <div style="font-family: Arial, sans-serif; padding: 16px; background-color: #f9f9f9; border-radius: 10px;">
                <h2 style="color: #1e88e5;">Nuevo comentario en una clase</h2>
                <p>El instructor ha dejado un nuevo comentario en la clase <b>%s</b>.</p>
                
                <p><b>Comentario:</b><br>%s</p>
                
                <p><b>Fecha de la clase:</b> %s<br>
                <b>Horario:</b> %s - %s</p>
                
                <p><b>Disciplina:</b> %s<br>
                <b>Instructor:</b> %s</p>
                
                <p><b>Fecha del comentario:</b> %s</p>
                
                <hr style="border: 0; border-top: 1px solid #ddd; margin: 16px 0;">
                <p style="color: #666;">Este mensaje fue generado automáticamente por el sistema <b>Qore</b>.</p>
            </div>
            """.formatted(
                        session.getName(),
                        session.getComentario(),
                        fechaClase,
                        horaInicio,
                        horaFin,
                        session.getDiscipline() != null ? session.getDiscipline().getName() : "N/A",
                        session.getInstructor() != null ? session.getInstructor().getName() : "N/A",
                        session.getComentarioAt() != null
                                ? session.getComentarioAt().format(commentFormatter)
                                : "N/A"
                );

                helper.setText(content, true);
                mailSender.send(message);
            }
        } catch (Exception e) {
            System.err.println("Error enviando correos de notificación: " + e.getMessage());
        }
    }





    private InstructorResponseDTO mapToDTO(Instructor instructor){

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
                .createdAt(instructor.getCreatedAt())
                .updatedAt(instructor.getUpdatedAt())
                .build();
    }
}
