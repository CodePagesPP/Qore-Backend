package com.example.Qore.controller;

import com.example.Qore.DTO.InstructorResponseDTO;
import com.example.Qore.DTO.InstructorStatsDTO;
import com.example.Qore.DTO.InstructorUpdateDTO;
import com.example.Qore.model.Instructor;
import com.example.Qore.model.Manager;
import com.example.Qore.model.User;
import com.example.Qore.repository.UserRepository;
import com.example.Qore.service.InstructorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/instructor")
public class InstructorController {
     private final InstructorService instructorService;

     private final UserRepository userRepository;

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return ResponseEntity.ok(user);
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
}
