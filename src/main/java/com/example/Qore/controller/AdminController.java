package com.example.Qore.controller;

import com.example.Qore.DTO.*;
import com.example.Qore.model.User;
import com.example.Qore.repository.UserRepository;
import com.example.Qore.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    private final InstructorService instructorService;

    private final StaffService staffService;

    private final ClientService clientService;

    private final ManagerService managerService;
    private final UserRepository userRepository;

    //Listar clientes
    @GetMapping("/clients")
    public ResponseEntity<List<ClientResponseDTO>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllActiveClients());
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

    @PostMapping("/registerClient")
    public ResponseEntity<ClientResponseDTO> registerClient(@Valid @RequestBody ClientRegisterDTO dto) {
        return ResponseEntity.ok(clientService.registerClient(dto));
    }

    @PostMapping("/registerManager")
    public ResponseEntity<ManagerResponseDTO> registerManager(@RequestBody ManagerRegisterDTO request){
        return ResponseEntity.ok(managerService.registerManager(request));
    }

    @PostMapping("/registerWorker")
    public ResponseEntity<UserResponseDTO> registerWorker(@Valid @RequestBody UserRegisterDTO dto) {
        return ResponseEntity.ok(userService.registerWorker(dto));
    }

    @DeleteMapping("/deleteWorker/{id}")
    public ResponseEntity<UserResponseDTO> deleteWorker(@PathVariable("id") Long id){
        userService.deleteWorker(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/updateWorker/{dni}")
    public ResponseEntity<UserResponseDTO> updateWorker(@PathVariable("dni") String dni ,@RequestBody UserUpdateDTO dto) {
        return ResponseEntity.ok(userService.updateWorker(dni, dto));
    }

    @GetMapping("/moreInfo/{id}")
    public ResponseEntity<UserResponseDTO> getMoreInfoById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/listPersonal")
    public ResponseEntity<Map<String, List<UserResponseDTO>>> listPersonal() {
        List<UserResponseDTO> users = userService.getAllNonClients();


        Map<String, List<UserResponseDTO>> grouped =
                users.stream().collect(Collectors.groupingBy(UserResponseDTO::getRole));

        return ResponseEntity.ok(grouped);
    }

    @GetMapping("/count-non-admin-client")
    public ResponseEntity<Map<String, Long>> countUsersNotAdminOrClient() {
        long count = userService.countUsersNotAdminOrClient();

        Map<String, Long> response = new HashMap<>();
        response.put("workers", count);

        return ResponseEntity.ok(response);
    }

}
