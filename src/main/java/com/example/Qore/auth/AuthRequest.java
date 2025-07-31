package com.example.Qore.auth;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}

