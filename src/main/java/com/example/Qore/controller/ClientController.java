package com.example.Qore.controller;

import com.example.Qore.DTO.ClientResponseDTO;
import com.example.Qore.DTO.ClientUpdateDTO;
import com.example.Qore.model.User;
import com.example.Qore.repository.UserRepository;
import com.example.Qore.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client")
public class ClientController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientService clientService;


    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return ResponseEntity.ok(user);
    }

    @GetMapping("/client/{id}")
    public ResponseEntity<ClientResponseDTO> getClient(@PathVariable String id) {
        return ResponseEntity.ok(clientService.getClientByDni(id));
    }

    @PatchMapping("/client/{id}")
    public ResponseEntity<ClientResponseDTO> updateClient(@PathVariable String id, @RequestBody ClientUpdateDTO dto) {
        return ResponseEntity.ok(clientService.updateClient(id, dto));
    }

    @DeleteMapping("/client/{id}")
    public ResponseEntity<Void> disableClient(@PathVariable String id) {
        clientService.disableClient(id);
        return ResponseEntity.noContent().build();
    }

}
