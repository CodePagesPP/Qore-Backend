package com.example.Qore.controller;

import com.example.Qore.DTO.*;
import com.example.Qore.model.ClassSession;
import com.example.Qore.model.Enum.EstadoSession;
import com.example.Qore.model.Instructor;
import com.example.Qore.model.Manager;
import com.example.Qore.model.User;
import com.example.Qore.repository.ClassSessionRepository;
import com.example.Qore.repository.UserRepository;
import com.example.Qore.service.InstructorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/instructor")
public class InstructorController {
     private final InstructorService instructorService;

     private final UserRepository userRepository;

     private final ClassSessionRepository classSessionRepository;

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return ResponseEntity.ok(user);
    }

    @GetMapping("/instructor/{dni}")
    public ResponseEntity<InstructorResponseDTO> getInstructor(@PathVariable String dni) {
        return ResponseEntity.ok(instructorService.getInstructorByDni(dni));
    }

     @GetMapping("/listInstructor")
    public ResponseEntity<List<InstructorResponseDTO>> listInstructor() {
         return ResponseEntity.ok(instructorService.getAllInstructors());
     }

     @PutMapping("/updateInstructor/{dni}")
    public ResponseEntity<InstructorResponseDTO> updateInstructor(@PathVariable("dni") String dni, @RequestBody InstructorUpdateDTO instructor) {
         return ResponseEntity.ok(instructorService.updateInstructor(dni, instructor));
     }

     @DeleteMapping("/deleteInstructor/{dni}")
    public ResponseEntity<Void> deleteInstructor(@PathVariable("dni") String dni) {
         instructorService.deleteInstructor(dni);
         return ResponseEntity.noContent().build();
     }

    @GetMapping("/{id}/stats")
    public ResponseEntity<InstructorStatsDTO> getInstructorStats(
            @PathVariable Long id,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(instructorService.getInstructorStats(id, month, year));
    }

    @PatchMapping("/{id}/comentario")
    public ResponseEntity<?> updateComentario(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {

        String comentario = payload.get("comentario");
        ClassSession session = classSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Clase no encontrada"));

        if (!session.getEstado().equals(EstadoSession.DICTADA)) {
            return ResponseEntity.badRequest().body("La clase no está dictada");
        }

        if (session.getComentarioAt() != null &&
                session.getComentarioAt().plusHours(24).isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest()
                    .body("El tiempo para editar el comentario ha expirado");
        }

        session.setComentario(comentario);
        session.setComentarioAt(LocalDateTime.now());
        classSessionRepository.save(session);
        instructorService.sendComentarioNotificationEmail(session);
        ClassSessionComentarioDTO dto = new ClassSessionComentarioDTO(
                session.getId(),
                session.getComentario(),
                session.getEstado(),
                session.getComentarioAt()
        );

        return ResponseEntity.ok(dto);
    }


}
