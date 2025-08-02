package com.example.Qore.DTO;

import com.example.Qore.model.Role;
import com.example.Qore.model.RoleE;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor

public class AdminDTO {
    private String email;
    private String password;
    private String role;
}
