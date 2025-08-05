package com.example.Qore.DTO;

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
    private String role;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
