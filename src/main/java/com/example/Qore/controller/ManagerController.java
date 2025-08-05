package com.example.Qore.controller;

import com.example.Qore.DTO.ManagerResponseDTO;
import com.example.Qore.DTO.ManagerUpdateDTO;
import com.example.Qore.DTO.StaffResponseDTO;
import com.example.Qore.DTO.StaffUpdateDTO;
import com.example.Qore.model.Manager;
import com.example.Qore.model.User;
import com.example.Qore.repository.ManagerRepository;
import com.example.Qore.repository.UserRepository;
import com.example.Qore.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manager")
public class ManagerController {

    @Autowired
    private ManagerRepository userRepository;

    @Autowired
    private ManagerService managerService;

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return ResponseEntity.ok(user);
    }

    @GetMapping("/listManager")
    public ResponseEntity<List<ManagerResponseDTO>> listStaff(){
        return ResponseEntity.ok(managerService.getAllManagers());
    }

    @PutMapping("/updateManager/{dni}")
    public ResponseEntity<ManagerResponseDTO> updateStaff(@PathVariable("dni") String dni , @RequestBody ManagerUpdateDTO staff) {
        return ResponseEntity.ok(managerService.updateManager(dni, staff));
    }

    @DeleteMapping("/deleteManager/{dni}")
    public ResponseEntity<Void> deleteStaff(@PathVariable("dni") String dni) {
        managerService.deleteManager(dni);
        return ResponseEntity.noContent().build();
    }
}

