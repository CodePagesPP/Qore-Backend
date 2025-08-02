package com.example.Qore.controller;

import com.example.Qore.DTO.*;
import com.example.Qore.model.Role;
import com.example.Qore.model.User;
import com.example.Qore.repository.UserRepository;
import com.example.Qore.service.InstructorService;
import com.example.Qore.service.StaffService;
import com.example.Qore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    private final UserService userService;

    private final InstructorService instructorService;

    private final StaffService staffService;

    //Listar clientes
    @GetMapping("/list-client")
    public ResponseEntity<List<UserDTO>> listClients(){
        List<UserDTO> users = userRepository.findByRole(Role.CLIENT);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/listAdmin")
    public ResponseEntity<List<UserDTO>> listAdmin(){
        return ResponseEntity.ok(userService.getAllAdmins());
    }

    @PutMapping("/updateAdmin/{id}")
    public ResponseEntity<UserDTO> updateAdmin(@PathVariable("id") Long id, @RequestBody AdminDTO request){
        return ResponseEntity.ok(userService.updateAdmin(id, request));
    }

    @DeleteMapping("/deleteAdmin/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable("id") Long id){
        userService.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/registerInstructor")
    public ResponseEntity<InstructorResponseDTO> registerInstructor(@RequestBody InstructorRegisterDTO request){
        return ResponseEntity.ok(instructorService.registerInstructor(request));
    }

    @PostMapping("/registerStaff")
    public ResponseEntity<StaffResponseDTO> registerStaff(@RequestBody StaffRegisterDTO request){
        return ResponseEntity.ok(staffService.registerStaff(request));
    }
}
