package com.example.Qore.controller;

import com.example.Qore.DTO.*;
import com.example.Qore.auth.AuthRequest;
import com.example.Qore.auth.AuthResponse;
import com.example.Qore.auth.RegisterRequest;
import com.example.Qore.auth.jwt.JwtUtil;
import com.example.Qore.model.Role;
import com.example.Qore.repository.UserRepository;
import com.example.Qore.service.AuthService;
import com.example.Qore.service.ClientService;
import com.example.Qore.service.UserDetailsServiceImpl;
import com.example.Qore.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")

@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthService authService;

    private final UserService userService;
    private final ClientService clientService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/registerAdmin")
    public ResponseEntity<UserDTO> register(@RequestBody AdminDTO request){
        return ResponseEntity.ok(userService.registerAdmin(request));
    }

    @PostMapping("/registerClient")
    public ResponseEntity<ClientResponseDTO> registerClient(@Valid @RequestBody ClientRegisterDTO dto) {
        return ResponseEntity.ok(clientService.registerClient(dto));
    }

    @GetMapping("/clients")
    public ResponseEntity<List<ClientResponseDTO>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllActiveClients());
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(jwt));
    }


}
