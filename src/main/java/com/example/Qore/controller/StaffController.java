package com.example.Qore.controller;

import com.example.Qore.DTO.StaffResponseDTO;
import com.example.Qore.DTO.StaffUpdateDTO;
import com.example.Qore.model.Staff;
import com.example.Qore.model.User;
import com.example.Qore.repository.UserRepository;
import com.example.Qore.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/staff")
public class StaffController {
    private final StaffService staffService;

    private final UserRepository userRepository;

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return ResponseEntity.ok(user);
    }

    @GetMapping("/listStaff")
    public ResponseEntity<List<StaffResponseDTO>> listStaff(){
        return ResponseEntity.ok(staffService.getAllStaff());
    }

    @PutMapping("/updateStaff/{dni}")
    public ResponseEntity<StaffResponseDTO> updateStaff(@PathVariable("dni") String dni ,@RequestBody StaffUpdateDTO staff) {
        return ResponseEntity.ok(staffService.updateStaff(dni, staff));
    }

    @DeleteMapping("/deleteStaff/{dni}")
    public ResponseEntity<Void> deleteStaff(@PathVariable("dni") String dni) {
        staffService.deleteStaff(dni);
        return ResponseEntity.noContent().build();
    }
}
