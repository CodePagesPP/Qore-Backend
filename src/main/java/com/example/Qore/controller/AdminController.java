package com.example.Qore.controller;

import com.example.Qore.model.Role;
import com.example.Qore.model.User;
import com.example.Qore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/list-client")
    public ResponseEntity<List<User>> listClients(){
        List<User> users = userRepository.findByRole(Role.CLIENT);
        return ResponseEntity.ok(users);
    }
}
