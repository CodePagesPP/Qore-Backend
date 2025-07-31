package com.example.Qore.auth;

import com.example.Qore.model.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private Role role;
}

