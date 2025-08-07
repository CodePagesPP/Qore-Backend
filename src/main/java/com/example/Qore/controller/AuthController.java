package com.example.Qore.controller;


import com.example.Qore.DTO.*;
import com.example.Qore.auth.AuthRequest;
import com.example.Qore.auth.AuthResponse;
import com.example.Qore.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")

@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final UserService userService;


    @PostMapping("/registerAdmin")
    public ResponseEntity<UserDTO> register(@RequestBody AdminDTO request){
        return ResponseEntity.ok(userService.registerAdmin(request));
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request){

        final String jwt = authService.login(request);

        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}
