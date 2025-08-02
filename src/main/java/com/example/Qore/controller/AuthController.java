package com.example.Qore.controller;

import com.example.Qore.DTO.AdminDTO;
import com.example.Qore.DTO.InstructorRegisterDTO;
import com.example.Qore.DTO.InstructorResponseDTO;
import com.example.Qore.DTO.UserDTO;
import com.example.Qore.auth.AuthRequest;
import com.example.Qore.auth.AuthResponse;
import com.example.Qore.auth.jwt.JwtUtil;
import com.example.Qore.repository.UserRepository;
import com.example.Qore.service.AuthService;
import com.example.Qore.service.InstructorService;
import com.example.Qore.service.UserDetailsServiceImpl;
import com.example.Qore.service.UserService;
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

    private final InstructorService instructorService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/registerAdmin")
    public ResponseEntity<UserDTO> register(@RequestBody AdminDTO request){
        return ResponseEntity.ok(userService.registerAdmin(request));
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
}
