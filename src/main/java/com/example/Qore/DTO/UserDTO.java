package com.example.Qore.DTO;

import com.example.Qore.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserDTO {
    private long id;
    private String email;
    private Role role;
    private Timestamp createdAt;
}
